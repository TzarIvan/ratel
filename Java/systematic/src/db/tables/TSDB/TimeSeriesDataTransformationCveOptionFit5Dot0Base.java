package db.tables.TSDB;

import db.*;
import db.columns.*;

public class TimeSeriesDataTransformationCveOptionFit5Dot0Base extends Table {

    private static final long serialVersionUID = 1L;    public static final TimeSeriesDataTransformationCveOptionFit5Dot0Base T_TIME_SERIES_DATA_TRANSFORMATION_CVE_OPTION_FIT_5DOT0 = new TimeSeriesDataTransformationCveOptionFit5Dot0Base("time_series_data_transformation_cve_option_fit_5dot0base");

    public TimeSeriesDataTransformationCveOptionFit5Dot0Base(String alias) { super("TSDB..time_series_data_transformation_cve_option_fit_5dot0", alias); }

    public IntColumn C_TIME_SERIES_ID = new IntColumn("time_series_id", "int", this, NOT_NULL);
    public IntColumn C_DATA_SOURCE_ID = new IntColumn("data_source_id", "int", this, NOT_NULL);
    public DatetimeColumn C_OBSERVATION_TIME = new DatetimeColumn("observation_time", "datetime", this, NOT_NULL);
    public FloatColumn C_OBSERVATION_VALUE = new FloatColumn("observation_value", "float(53)", this, NOT_NULL);


}

