package com.lvl6.ui.admin.components;

import com.googlecode.wicketcharts.highcharts.options.Options;

public class TimeSeriesLineChartOptions extends Options {

	private static final long serialVersionUID = 1L;

	/*	@SuppressWarnings("unchecked")
		public TimeSeriesLineChartOptions(String title, String yAxisTitle, List<RollupEntry> entries) {
			super();
			List<String> times = new ArrayList<String>();
			List<Number> values = new ArrayList<Number>();
			for(RollupEntry ent : entries) {
				times.add(ent.getColumnDisplayName());
				values.add(ent.getValue());
			}
			setTitle(new Title(title));
			XAxis xAxis = new XAxis();
			xAxis.setTitle(new Title("Time"));
			xAxis.setCategories(times);
			YAxis yAxis = new YAxis();
			yAxis.setTitle(new Title(yAxisTitle));
			setxAxis(xAxis);
			setyAxis(yAxis);
			SeriesOptions<Number> series = new SimpleSeriesOptions();
			series.setData(values);
			setSeries(Arrays.asList(series));
		}
	*/

}
