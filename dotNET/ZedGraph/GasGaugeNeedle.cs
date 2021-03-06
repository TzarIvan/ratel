using System;
using System.Drawing;
using System.Drawing.Drawing2D;
using System.Runtime.Serialization;
using System.Security.Permissions;

namespace ZedGraph {
    /// <summary>
    /// A class representing a needle on the GasGuage chart
    /// <see cref="GasGaugeNeedle"/>s.
    /// </summary>
    /// <author> Jay Mistry </author>
    /// <version> $Revision: 1.2 $ $Date: 2007-08-11 14:37:47 $ </version>
    [Serializable] public class GasGaugeNeedle : CurveItem, ICloneable {
        #region Fields
        /// <summary>
        /// Private field that stores the <see cref="Border"/> class that defines the
        /// properties of the border around this <see cref="GasGaugeNeedle"/>. Use the public
        /// property <see cref="Border"/> to access this value.
        /// </summary>
        Border _border;

        /// <summary>
        /// The bounding rectangle for this <see cref="GasGaugeNeedle"/>.
        /// </summary>
        RectangleF _boundingRectangle;
        /// <summary>
        /// Color of the needle line
        /// </summary>
        Color _color;

        /// <summary>
        /// Private field that stores the <see cref="ZedGraph.Fill"/> data for this
        /// <see cref="GasGaugeNeedle"/>. Use the public property <see cref="Fill"/> to
        /// access this value.
        /// </summary>
        Fill _fill;

        /// <summary>
        /// A <see cref="ZedGraph.TextObj"/> which will customize the label display of this
        /// <see cref="GasGaugeNeedle"/>
        /// </summary>
        TextObj _labelDetail;
        /// <summary>
        /// Value of this needle
        /// </summary>
        double _needleValue;

        /// <summary>
        /// Internally calculated angle that places this needle relative to the MinValue and
        /// MaxValue of 180 degree GasGuage
        /// </summary>
        float _sweepAngle;
        #endregion
        #region Constructors
        /// <summary>
        /// Create a new <see cref="GasGaugeNeedle"/>
        /// </summary>
        /// <param name="label">The value associated with this <see cref="GasGaugeNeedle"/>
        /// instance.</param>
        /// <param name="color">The display color for this <see cref="GasGaugeNeedle"/>
        /// instance.</param>
        /// <param name="val">The value of this <see cref="GasGaugeNeedle"/>.</param>
        public GasGaugeNeedle(string label, double val, Color color)
            : base(label) {
            NeedleValue = val;
            NeedleColor = color;
            NeedleWidth = Default.NeedleWidth;
            SweepAngle = 0f;
            _border = new Border(Default.BorderColor, Default.BorderWidth);
            _labelDetail = new TextObj {FontSpec = {Size = Default.FontSize}};
            SlicePath = null;
        }

        /// <summary>
        /// The Copy Constructor
        /// </summary>
        /// <param name="ggn">The <see cref="GasGaugeNeedle"/> object from which to copy</param>
        public GasGaugeNeedle(GasGaugeNeedle ggn)
            : base(ggn) {
            NeedleValue = ggn.NeedleValue;
            NeedleColor = ggn.NeedleColor;
            NeedleWidth = ggn.NeedleWidth;
            SweepAngle = ggn.SweepAngle;
            _border = ggn.Border.Clone();
            _labelDetail = ggn.LabelDetail.Clone();
            _labelDetail.FontSpec.Size = ggn.LabelDetail.FontSpec.Size;
        }

        /// <summary>
        /// Implement the <see cref="ICloneable" /> interface in a typesafe manner by just
        /// calling the typed version of <see cref="Clone" />
        /// </summary>
        /// <returns>A deep copy of this object</returns>
        object ICloneable.Clone() {
            return Clone();
        }

        /// <summary>
        /// Typesafe, deep-copy clone method.
        /// </summary>
        /// <returns>A new, independent copy of this class</returns>
        public GasGaugeNeedle Clone() {
            return new GasGaugeNeedle(this);
        }
        #endregion
        #region Properties
        /// <summary>
        /// Gets or Sets the NeedleWidth of this <see cref="GasGaugeNeedle"/>
        /// </summary>
        public float NeedleWidth { get; set; }

        /// <summary>
        /// Gets or Sets the Border of this <see cref="GasGaugeNeedle"/>
        /// </summary>
        public Border Border {
            get { return (_border); }
            set { _border = value; }
        }

        /// <summary>
        /// Gets or Sets the SlicePath of this <see cref="GasGaugeNeedle"/>
        /// </summary>
        public GraphicsPath SlicePath { get; set; }

        /// <summary>
        /// Gets or Sets the LableDetail of this <see cref="GasGaugeNeedle"/>
        /// </summary>
        public TextObj LabelDetail {
            get { return _labelDetail; }
            set { _labelDetail = value; }
        }

        /// <summary>
        /// Gets or Sets the NeedelColor of this <see cref="GasGaugeNeedle"/>
        /// </summary>
        public Color NeedleColor {
            get { return _color; }
            set {
                _color = value;
                Fill = new Fill(_color);
            }
        }

        /// <summary>
        /// Gets or Sets the Fill of this <see cref="GasGaugeNeedle"/>
        /// </summary>
        public Fill Fill {
            get { return _fill; }
            set { _fill = value; }
        }

        /// <summary>
        /// Private property that Gets or Sets the SweepAngle of this <see cref="GasGaugeNeedle"/>
        /// </summary>
        float SweepAngle {
            get { return _sweepAngle; }
            set { _sweepAngle = value; }
        }

        /// <summary>
        /// Gets or Sets the NeedleValue of this <see cref="GasGaugeNeedle"/>
        /// </summary>
        public double NeedleValue {
            get { return (_needleValue); }
            set { _needleValue = value > 0 ? value : 0; }
        }

        /// <summary>
        /// Gets a flag indicating if the Z data range should be included in the axis scaling calculations.
        /// </summary>
        /// <param name="pane">The parent <see cref="GraphPane" /> of this <see cref="CurveItem" />.
        /// </param>
        /// <value>true if the Z data are included, false otherwise</value>
        internal override bool IsZIncluded(GraphPane pane) {
            return false;
        }

        /// <summary>
        /// Gets a flag indicating if the X axis is the independent axis for this <see cref="CurveItem" />
        /// </summary>
        /// <param name="pane">The parent <see cref="GraphPane" /> of this <see cref="CurveItem" />.
        /// </param>
        /// <value>true if the X axis is independent, false otherwise</value>
        internal override bool IsXIndependent(GraphPane pane) {
            return true;
        }
        #endregion
        #region Serialization
        /// <summary>
        /// Current schema value that defines the version of the serialized file
        /// </summary>
        public const int schema2 = 10;

        /// <summary>
        /// Constructor for deserializing objects
        /// </summary>
        /// <param name="info">A <see cref="SerializationInfo"/> instance that defines the serialized data
        /// </param>
        /// <param name="context">A <see cref="StreamingContext"/> instance that contains the serialized data
        /// </param>
        protected GasGaugeNeedle(SerializationInfo info, StreamingContext context)
            : base(info, context) {
            // The schema value is just a file version parameter. You can use it to make future versions
            // backwards compatible as new member variables are added to classes
            var sch = info.GetInt32("schema2");

            _labelDetail = (TextObj) info.GetValue("labelDetail", typeof (TextObj));
            _fill = (Fill) info.GetValue("fill", typeof (Fill));
            _border = (Border) info.GetValue("border", typeof (Border));
            _needleValue = info.GetDouble("needleValue");
            _boundingRectangle = (RectangleF) info.GetValue("boundingRectangle", typeof (RectangleF));
            SlicePath = (GraphicsPath) info.GetValue("slicePath", typeof (GraphicsPath));
            _sweepAngle = (float) info.GetDouble("sweepAngle");
            _color = (Color) info.GetValue("color", typeof (Color));
        }

        /// <summary>
        /// Populates a <see cref="SerializationInfo"/> instance with the data needed to serialize the target object
        /// </summary>
        /// <param name="info">A <see cref="SerializationInfo"/> instance that defines the serialized data</param>
        /// <param name="context">A <see cref="StreamingContext"/> instance that contains the serialized data</param>
        [SecurityPermission(SecurityAction.Demand, SerializationFormatter = true)] public override void GetObjectData(
            SerializationInfo info, StreamingContext context) {
            base.GetObjectData(info, context);
            info.AddValue("schema2", schema2);
            info.AddValue("labelDetail", _labelDetail);
            info.AddValue("fill", _fill);
            info.AddValue("border", _border);
            info.AddValue("needleValue", _needleValue);
            info.AddValue("boundingRectangle", _boundingRectangle);
            info.AddValue("slicePath", SlicePath);
            info.AddValue("sweepAngle", _sweepAngle);
        }
        #endregion
        #region Default
        /// <summary>
        /// Specify the default property values for the <see cref="GasGaugeNeedle"/> class.
        /// </summary>
        public struct Default {
            /// <summary>
            /// The default color for drawing frames around GasGaugeNeedle
            /// (<see cref="ZedGraph.LineBase.Color"/> property).
            /// </summary>
            public static Color BorderColor = Color.Gray;
            /// <summary>
            /// The default pen width to be used for drawing the border around the GasGaugeNeedle
            /// (<see cref="ZedGraph.LineBase.Width"/> property). Units are points.
            /// </summary>
            public static float BorderWidth = 1.0F;

            /// <summary>
            /// The default custom brush for filling in the GasGaugeNeedle.
            /// (<see cref="ZedGraph.Fill.Brush"/> property).
            /// </summary>
            public static Brush FillBrush;
            /// <summary>
            /// The default color for filling in the GasGaugeNeedle
            /// (<see cref="ZedGraph.Fill.Color"/> property).
            /// </summary>
            public static Color FillColor = Color.Empty;
            /// <summary>
            /// The default fill type for filling the GasGaugeNeedle.
            /// </summary>
            public static FillType FillType = FillType.Brush;

//			/// <summary>
//			/// Default value for <see cref="GasGaugeNeedle.LabelType"/>.
//			/// </summary>
//			public static PieLabelType LabelType = PieLabelType.Name;

            /// <summary>
            /// The default font size for <see cref="GasGaugeNeedle.LabelDetail"/> entries
            /// (<see cref="ZedGraph.FontSpec.Size"/> property). Units are
            /// in points (1/72 inch).
            /// </summary>
            public static float FontSize = 10;
            /// <summary>
            /// The default border mode for GasGaugeNeedle (<see cref="ZedGraph.LineBase.IsVisible"/>
            /// property).
            /// true to display frame around GasGaugeNeedle, false otherwise
            /// </summary>
            public static bool IsBorderVisible = true;
            /// <summary>
            ///Default value for controlling <see cref="GasGaugeNeedle"/> display.
            /// </summary>
            public static bool isVisible = true;
            /// <summary>
            /// The default width of the gas gauge needle.  Units are points, scaled according
            /// to <see cref="PaneBase.CalcScaleFactor" />
            /// </summary>
            public static float NeedleWidth = 10.0F;
        }
        #endregion Defaults
        #region Methods
        /// <summary>
        /// Do all rendering associated with this <see cref="GasGaugeNeedle"/> item to the specified
        /// <see cref="Graphics"/> device. This method is normally only
        /// called by the Draw method of the parent <see cref="ZedGraph.CurveList"/>
        /// collection object.
        /// </summary>
        /// <param name="g">
        /// A graphic device object to be drawn into. This is normally e.Graphics from the
        /// PaintEventArgs argument to the Paint() method.
        /// </param>
        /// <param name="pane">
        /// A reference to the <see cref="ZedGraph.GraphPane"/> object that is the parent or
        /// owner of this object.
        /// </param>
        /// <param name="pos">Not used for rendering GasGaugeNeedle</param>
        /// <param name="scaleFactor">Not used for rendering GasGaugeNeedle</param>
        public override void Draw(Graphics g, GraphPane pane, int pos, float scaleFactor) {
            if (pane.Chart._rect.Width <= 0 && pane.Chart._rect.Height <= 0) SlicePath = null;
            else {
                CalcRectangle(g, pane, scaleFactor, pane.Chart._rect);

                SlicePath = new GraphicsPath();

                if (!_isVisible) return;

                var tRect = _boundingRectangle;

                if (tRect.Width >= 1 && tRect.Height >= 1) {
                    var sMode = g.SmoothingMode;
                    g.SmoothingMode = SmoothingMode.AntiAlias;

                    var matrix = new Matrix();

                    matrix.Translate(tRect.X + (tRect.Width / 2), tRect.Y + (tRect.Height / 2), MatrixOrder.Prepend);

                    var pts = new PointF[2];
                    pts[0] =
                        new PointF(
                            ((tRect.Height * .10f) / 2.0f) * (float) Math.Cos(-SweepAngle * Math.PI / 180.0f),
                            ((tRect.Height * .10f) / 2.0f) * (float) Math.Sin(-SweepAngle * Math.PI / 180.0f));
                    pts[1] = new PointF(
                        (tRect.Width / 2.0f) * (float) Math.Cos(-SweepAngle * Math.PI / 180.0f),
                        (tRect.Width / 2.0f) * (float) Math.Sin(-SweepAngle * Math.PI / 180.0f));

                    matrix.TransformPoints(pts);

                    var p = new Pen(NeedleColor, ((tRect.Height * .10f) / 2.0f)) {EndCap = LineCap.ArrowAnchor};
                    g.DrawLine(p, pts[0].X, pts[0].Y, pts[1].X, pts[1].Y);

                    //Fill center 10% with Black dot;
                    var f = new Fill(Color.Black);
                    var r = new RectangleF(
                        (tRect.X + (tRect.Width / 2)) - 1.0f, (tRect.Y + (tRect.Height / 2)) - 1.0f, 1.0f, 1.0f);
                    r.Inflate((tRect.Height * .10f), (tRect.Height * .10f));
                    var b = f.MakeBrush(r);
                    g.FillPie(b, r.X, r.Y, r.Width, r.Height, 0.0f, -180.0f);

                    var borderPen = new Pen(Color.White, 2.0f);
                    g.DrawPie(borderPen, r.X, r.Y, r.Width, r.Height, 0.0f, -180.0f);

                    g.SmoothingMode = sMode;
                }
            }
        }

        /// <summary>
        /// Render the label for this <see cref="GasGaugeNeedle"/>.
        /// </summary>
        /// <param name="g">
        /// A graphic device object to be drawn into. This is normally e.Graphics from the
        /// PaintEventArgs argument to the Paint() method.
        /// </param>
        /// <param name="pane">
        /// A graphic device object to be drawn into. This is normally e.Graphics from the
        /// PaintEventArgs argument to the Paint() method.
        /// </param>
        /// <param name="rect">Bounding rectangle for this <see cref="GasGaugeNeedle"/>.</param>
        /// <param name="scaleFactor">
        /// The scaling factor to be used for rendering objects. This is calculated and
        /// passed down by the parent <see cref="ZedGraph.GraphPane"/> object using the
        /// <see cref="PaneBase.CalcScaleFactor"/> method, and is used to proportionally adjust
        /// font sizes, etc. according to the actual size of the graph.
        /// </param> 
        public override void DrawLegendKey(Graphics g, GraphPane pane, RectangleF rect, float scaleFactor) {
            if (!_isVisible) return;

            var yMid = rect.Top + rect.Height / 2.0F;

            var pen = new Pen(NeedleColor, pane.ScaledPenWidth(NeedleWidth / 2, scaleFactor)) {
                StartCap = LineCap.Round,
                EndCap = LineCap.ArrowAnchor,
                DashStyle = DashStyle.Solid
            };
            g.DrawLine(pen, rect.Left, yMid, rect.Right, yMid);
        }

        /// <summary>
        /// Determine the coords for the rectangle associated with a specified point for 
        /// this <see cref="CurveItem" />
        /// </summary>
        /// <param name="pane">The <see cref="GraphPane" /> to which this curve belongs</param>
        /// <param name="i">The index of the point of interest</param>
        /// <param name="coords">A list of coordinates that represents the "rect" for
        /// this point (used in an html AREA tag)</param>
        /// <returns>true if it's a valid point, false otherwise</returns>
        public override bool GetCoords(GraphPane pane, int i, out string coords) {
            coords = String.Empty;
            return false;
        }

        /// <summary>
        /// Calculate the values needed to properly display this <see cref="GasGaugeNeedle"/>.
        /// </summary>
        /// <param name="pane">
        /// A graphic device object to be drawn into. This is normally e.Graphics from the
        /// PaintEventArgs argument to the Paint() method.
        /// </param>
        public static void CalculateGasGaugeParameters(GraphPane pane) {
            //loop thru slices and get total value and maxDisplacement
            var minVal = double.MaxValue;
            var maxVal = double.MinValue;
            foreach (var curve in pane.CurveList)
                if (curve is GasGaugeRegion) {
                    var ggr = (GasGaugeRegion) curve;
                    if (maxVal < ggr.MaxValue) maxVal = ggr.MaxValue;

                    if (minVal > ggr.MinValue) minVal = ggr.MinValue;
                }

            //Set Needle Sweep angle values here based on the min and max values of the GasGuage
            foreach (var curve in pane.CurveList)
                if (curve is GasGaugeNeedle) {
                    var ggn = (GasGaugeNeedle) curve;
                    var sweep = ((float) ggn.NeedleValue - (float) minVal) /
                        ((float) maxVal - (float) minVal) * 180.0f;
                    ggn.SweepAngle = sweep;
                }
        }

        /// <summary>
        /// Calculate the <see cref="RectangleF"/> that will be used to define the bounding rectangle of
        /// the GasGaugeNeedle.
        /// </summary>
        /// <remarks>This rectangle always lies inside of the <see cref="Chart.Rect"/>, and it is
        /// normally a square so that the pie itself is not oval-shaped.</remarks>
        /// <param name="g">
        /// A graphic device object to be drawn into. This is normally e.Graphics from the
        /// PaintEventArgs argument to the Paint() method.
        /// </param>
        /// <param name="pane">
        /// A reference to the <see cref="ZedGraph.GraphPane"/> object that is the parent or
        /// owner of this object.
        /// </param>
        /// <param name="scaleFactor">
        /// The scaling factor to be used for rendering objects. This is calculated and
        /// passed down by the parent <see cref="ZedGraph.GraphPane"/> object using the
        /// <see cref="PaneBase.CalcScaleFactor"/> method, and is used to proportionally adjust
        /// font sizes, etc. according to the actual size of the graph.
        /// </param> 
        /// <param name="chartRect">The <see cref="RectangleF"/> (normally the <see cref="Chart.Rect"/>)
        /// that bounds this pie.</param>
        /// <returns></returns>
        public static RectangleF CalcRectangle(Graphics g, GraphPane pane, float scaleFactor, RectangleF chartRect) {
            var nonExpRect = chartRect;

            if ((2 * nonExpRect.Height) > nonExpRect.Width) {
                //Scale based on width
                var percentS = ((nonExpRect.Height * 2) - nonExpRect.Width) / (nonExpRect.Height * 2);
                nonExpRect.Height = ((nonExpRect.Height * 2) - ((nonExpRect.Height * 2) * percentS));
            } else nonExpRect.Height = nonExpRect.Height * 2;

            nonExpRect.Width = nonExpRect.Height;

            var xDelta = (chartRect.Width / 2) - (nonExpRect.Width / 2);

            //Align Horizontally
            nonExpRect.X += xDelta;

            nonExpRect.Inflate(-0.05F * nonExpRect.Height, -(float) 0.05 * nonExpRect.Width);

            CalculateGasGaugeParameters(pane);

            foreach (var curve in pane.CurveList)
                if (curve is GasGaugeNeedle) {
                    var ggn = (GasGaugeNeedle) curve;
                    ggn._boundingRectangle = nonExpRect;
                }

            return nonExpRect;
        }
        #endregion
    }
}