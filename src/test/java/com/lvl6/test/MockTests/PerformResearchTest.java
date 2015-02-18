package com.lvl6.test.MockTests;

import org.junit.Before;
import static org.mockito.Mockito.*;
import com.lvl6.info.User;


public class PerformResearchTest {

	private static User mockUser;
	
	@Before
	public void setUp() throws Exception {
		mockUser = mock(User.class);
		
		when(mockUser.getGems().thenReturn(100));
		
		
		
	}
	
	
	
}
