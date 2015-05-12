package db.tables.TSDB;

import db.*;
import db.columns.*;

public class TimeSeriesDataCdsCompositeDepthBase extends Table {

    private static final long serialVersionUID = 1L;    public static final TimeSeriesDataCdsCompositeDepthBase T_TIME_SERIES_DATA_CDS_COMPOSITE_DEPTH = new TimeSeriesDataCdsCompositeDepthBase("time_series_data_cds_composite_depthbase");

    public TimeSeriesDataCdsCompositeDepthBase(String alias) { super("TSDB..time_series_data_cds_composite_depth", alias); }

    public IntColumn C_TIME_SERIES_ID = new IntColumn("time_series_id", "int", this, NOT_NULL);
    public IntColumn C_DATA_SOURCE_ID = new IntColumn("data_source_id", "int", this, NOT_NULL);
    public DatetimeColumn C_OBSERVATION_TIME = new DatetimeColumn("observation_time", "datetime", this, NOT_NULL);
    public FloatColumn C_OBSERVATION_VALUE = new FloatColumn("observation_value", "float(53)", this, NOT_NULL);


}
