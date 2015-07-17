package com.lvl6.server.controller.actionobjects;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.sql.Timestamp;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lvl6.info.User;
import com.lvl6.misc.MiscMethods;
import com.lvl6.properties.ControllerConstants;
import com.lvl6.properties.IAPValues;
import com.lvl6.proto.EventInAppPurchaseProto.InAppPurchaseResponseProto.Builder;
import com.lvl6.proto.EventInAppPurchaseProto.InAppPurchaseResponseProto.InAppPurchaseStatus;
import com.lvl6.retrieveutils.UserRetrieveUtils2;
import com.lvl6.utils.utilmethods.InsertUtil;

public class InAppPurchaseAction
{
    private static Logger log = LoggerFactory.getLogger(new Object() {
    }.getClass().getEnclosingClass());

    //TODO: get the real value
    private static final String ANDROID_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAjz24BuNmGZ3HfL63fuPj2n/JbRbElGfXdEqtT5maY9X9PRYlz9cXvvyireW4R1/4nNxN2yw3ow3vDfzDqBDLwt4+AWMVvuGUBn0gy+UcE1xUIlcPuZVjuwbsCo6s2+SthsdezxouVqfFX2S3MxiwjeW0iPV/YOnz/a/EoaaTIGNCrkp0DnNhtCsXV09vEabU98Rp4iffDB9BVQC957EA3jGAMms2eVaqy4CqDysaJQCvnSa7BfALiCrbHVZiGuUf6oDxs1IxD+znwFLDujaC/oGGQbzKSqzKMJOpZtZ87hOT4C3dRwlsesmloU2eKEByCGXbEwLpBZofjB7C07ClTwIDAQAB";
    private static final String APPLE_SANDBOX_URL = "https://sandbox.itunes.apple.com/verifyReceipt";
    private static final String APPLE_PRODUCTION_URL = "https://buy.itunes.apple.com/verifyReceipt";

    private static final String ANDROID_KEY_FACTORY_ALGORITHM = "RSA";
    private static final String ANDROID_SIGNATURE_ALGORITHM = "SHA1withRSA";
    private static final String ANDROID_ORIGINAL_JSON = "originalJson";
    private static final String ANDROID_SIGNATURE = "signature";
    private static final String ANDROID_PACKAGE_NAME = "packageName";
    
    private String userId;
    private String receipt;
    private UserRetrieveUtils2 userRetrieveUtil;
    private InsertUtil insertUtil;

    public InAppPurchaseAction(
            String userId,
            String receipt,
            UserRetrieveUtils2 userRetrieveUtil,
            InsertUtil insertUtil )
    {
        super();
        this.userId = userId;
        this.receipt = receipt;
        this.userRetrieveUtil = userRetrieveUtil;
        this.insertUtil = insertUtil;
    }

    //	//encapsulates the return value from this Action Object
    //	static class SetDefendingMsgResource {
    //		
    //		
    //		public SetDefendingMsgResource() {
    //			
    //		}
    //	}
    //
    //	public SetDefendingMsgResource execute() {
    //		
    //	}

    //derived state
    private User user;
    private JSONObject jsonObject;
    private String packageName;
    private int gemsGained;
    private double realLifeCashCost;
    private boolean isAppleReceipt = false;

    private Map<String, Integer> currencyDeltas;
    private Map<String, Integer> prevCurrencies;
    private Map<String, Integer> curCurrencies;
    private Map<String, String> reasonsForChanges;
    private Map<String, String> details;


    public void execute(Builder resBuilder) {
        resBuilder.setStatus(InAppPurchaseStatus.FAIL);

        //check out inputs before db interaction
        boolean valid = verifySyntax(resBuilder);

        if (!valid) {
            return;
        }

        valid = verifySemantics(resBuilder);

        if (!valid) {
            return;
        }

        boolean success = writeChangesToDB(resBuilder);
        if (!success) {
            return;
        }

        resBuilder.setStatus(InAppPurchaseStatus.SUCCESS);
        resBuilder.setPackageName(packageName);
        resBuilder.setPackagePrice(realLifeCashCost);
        log.info("successful in-app purchase from user {} for package {}",
                userId, packageName );

    }

    private boolean verifySyntax(Builder resBuilder) {

        if (null == receipt || receipt.isEmpty()) {
            log.error("invalid receipt: {}", receipt);
            return false;
        }

        return true;
    }

    private boolean verifySemantics(Builder resBuilder) {
        user = userRetrieveUtil.getUserById(userId);
        if (null == user) {
            log.error( "no user with id={}", userId);
            return false;
        }
        
        if (isAppleReceipt) {
            if (!verifyAppleReceipt()) {
                log.error("error validating apple receipt");
                return false;
            }
        } else {
            if (!verifyGoogleReceipt()) {
                log.error("error validating google receipt");
                return false;
            }
        }

        return true;
    }

    private boolean verifyAppleReceipt() {
        boolean success = false;
        OutputStreamWriter wr = null;
        // Get the response
        BufferedReader rd = null;
        try {
            JSONObject response;
            JSONObject jsonReceipt = new JSONObject();
            jsonReceipt.put(IAPValues.RECEIPT_DATA, receipt);
            log.info("Processing purchase: {}", jsonReceipt.toString(4));
            // Send data
            URL url = new URL(APPLE_PRODUCTION_URL);

            log.info("Sending purchase request to: {}", url.toString());

            URLConnection conn = url.openConnection();
            conn.setDoOutput(true);
            wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write(jsonReceipt.toString());
            wr.flush();

            rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            String responseString = "";
            String line;
            while ((line = rd.readLine()) != null) {
                responseString += line;
            }
            log.info("Response: {}", responseString);

            response = new JSONObject(responseString);

            if (response.getInt(IAPValues.STATUS) == 21007 || response.getInt(IAPValues.STATUS) == 21008) {
                wr.close();
                rd.close();
                url = new URL(APPLE_SANDBOX_URL);
                conn = url.openConnection();
                conn.setDoOutput(true);
                wr = new OutputStreamWriter(conn.getOutputStream());
                wr.write(jsonReceipt.toString());
                wr.flush();
                rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                responseString = "";
                while ((line = rd.readLine()) != null) {
                    responseString += line;
                }
                response = new JSONObject(responseString);
            }

            jsonObject = null;
            if (response.getInt(IAPValues.STATUS) == 0) {
                jsonObject = response.getJSONObject(IAPValues.RECEIPT);
                packageName = jsonObject.getString(IAPValues.PRODUCT_ID);
                success = true;
            } else {
                log.error("problem with in-app purchase that client sent, with receipt {}", receipt);
            }
        } catch (Exception e) {
            log.error("verifyAppleReceipt",e);
        }

        
        try {
            if (wr != null) {
                wr.close();
            }
            if (rd != null) {
                rd.close();
            }
        } catch (IOException e) {
            log.error("closing objects error",e);
        }
        
        return success;
    }
    
    private boolean verifyGoogleReceipt() {
        boolean success = false;
        
        try {
            jsonObject = new JSONObject(receipt);
            
            String signedData = jsonObject.getString(ANDROID_ORIGINAL_JSON); 
            String signature = jsonObject.getString(ANDROID_SIGNATURE);
            
            success = verifyPurchase(ANDROID_KEY, signedData, signature);
            
            if (success) {
                JSONObject signedDataJson = new JSONObject(signedData);
                packageName = signedDataJson.getString(ANDROID_PACKAGE_NAME);
            }
        } catch(Exception e) {
            log.error("verifyAppleReceipt",e);
        }
        
        return success;
    }
    

    /**
     * Verifies that the data was signed with the given signature, and returns
     * the verified purchase. The data is in JSON format and signed
     * with a private key. The data also contains the {@link PurchaseState}
     * and product ID of the purchase.
     * @param base64PublicKey the base64-encoded public key to use for verifying.
     * @param signedData the signed JSON string (signed, not encrypted)
     * @param signature the signature for the data, signed with the private key
     */
    public static boolean verifyPurchase(String base64PublicKey, String signedData, String signature) {
        if (null == signedData || signedData.isEmpty() || null == base64PublicKey || base64PublicKey.isEmpty() ||
                null == signature || signature.isEmpty() ) {
            log.error("Purchase verification failed: missing data.");
            return false;
        }

        PublicKey key = generatePublicKey(base64PublicKey);
        return verify(key, signedData, signature);
    }

    /**
     * Generates a PublicKey instance from a string containing the
     * Base64-encoded public key.
     *
     * @param encodedPublicKey Base64-encoded public key
     * @throws IllegalArgumentException if encodedPublicKey is invalid
     */
    public static PublicKey generatePublicKey(String encodedPublicKey) {
        try {
            byte[] decodedKey = Base64.getDecoder().decode(encodedPublicKey);
            KeyFactory keyFactory = KeyFactory.getInstance(ANDROID_KEY_FACTORY_ALGORITHM);
            return keyFactory.generatePublic(new X509EncodedKeySpec(decodedKey));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (InvalidKeySpecException e) {
            log.error("Invalid key specification.");
            throw new IllegalArgumentException(e);
        } catch (Exception e) {
            log.error("Base64 decoding failed.");
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * Verifies that the signature from the server matches the computed
     * signature on the data.  Returns true if the data is correctly signed.
     *
     * @param publicKey public key associated with the developer account
     * @param signedData signed data from server
     * @param signature server signature
     * @return true if the data and signature match
     */
    public static boolean verify(PublicKey publicKey, String signedData, String signature) {
        Signature sig;
        try {
            sig = Signature.getInstance(ANDROID_SIGNATURE_ALGORITHM);
            sig.initVerify(publicKey);
            sig.update(signedData.getBytes());
            if (!sig.verify(Base64.getDecoder().decode(signature))) {
                log.error("Signature verification failed.");
                return false;
            }
            return true;
        } catch (NoSuchAlgorithmException e) {
            log.error("NoSuchAlgorithmException.");
        } catch (InvalidKeyException e) {
            log.error("Invalid key specification.");
        } catch (SignatureException e) {
            log.error("Signature exception.");
        } catch (Exception e) {
            log.error("Base64 decoding failed.");
        }
        return false;
    }
    
    private boolean writeChangesToDB(Builder resBuilder) {
        
        boolean isBeginnerSale = IAPValues.packageIsBeginnerSale(packageName);
        gemsGained = IAPValues.getDiamondsForPackageName(packageName);
        realLifeCashCost = IAPValues.getCashSpentForPackageName(packageName);
        int coinChange = 0;
        
        prevCurrencies = new HashMap<String, Integer>();
        if (0 != gemsGained) {
            prevCurrencies.put(MiscMethods.gems, user.getGems());
            
            resBuilder.setDiamondsGained(gemsGained);
            user.updateRelativeDiamondsBeginnerSale(gemsGained, isBeginnerSale);
        }
        
        if (!insertUtil.insertIAPHistoryElem(jsonObject,
                gemsGained, coinChange, user, realLifeCashCost)) {
            
            if (isAppleReceipt) {
                try {
                log.error("problem with logging in-app purchase history for receipt:{} and user {}",
                        jsonObject.toString(4), user);
                }
                catch (JSONException e) {
                    log.error("", e);
                }
            }
            return false;
        }

        writeCurrencyHistory();

        return true;
    }

    private void writeCurrencyHistory()
    {
        try {
            String gems = MiscMethods.gems;

            currencyDeltas = new HashMap<String, Integer>();
            curCurrencies = new HashMap<String, Integer>();
            reasonsForChanges = new HashMap<String, String>();
            details = new HashMap<String, String>();
            if (0 != gemsGained) {
                currencyDeltas.put(gems, gemsGained);
                curCurrencies.put(gems, user.getGems());
                reasonsForChanges.put(gems,
                        ControllerConstants.UCHRFC__IN_APP_PURCHASE);
                
                String detail = String.format("%s, cashIRL=%s",
                        packageName, realLifeCashCost);
                details.put(gems, detail);
            }
            
            Timestamp date = new Timestamp((new Date()).getTime());
            MiscMethods.writeToUserCurrencyOneUser(userId, date,
                    getCurrencyDeltas(), getPreviousCurrencies(),
                    getCurrentCurrencies(), getReasons(),
                    getDetails());
        } catch (Exception e) {
            log.error(
                    String.format(
                            "%s userId=%s, packageName=%s, gemsGained=%s, cashIRL=%s",
                            "unable to write to currency history.",
                            userId, packageName, gemsGained, realLifeCashCost),
                    e);
        }
        
    }

    public User getUser() {
        return user;
    }

    public Map<String, Integer> getCurrencyDeltas() {
        return currencyDeltas;
    }

    public Map<String, Integer> getPreviousCurrencies() {
        return prevCurrencies;
    }

    public Map<String, Integer> getCurrentCurrencies() {
        return curCurrencies;
    }

    public Map<String, String> getReasons() {
        return reasonsForChanges;
    }

    public Map<String, String> getDetails() {
        return details;
    }
}
