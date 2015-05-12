﻿using System;
using System.Drawing;
using System.Runtime.Serialization;
using System.Security.Permissions;

namespace ZedGraph {
    /// <summary>
    /// A class that represents a bordered and/or filled box (rectangle) object on
    /// the graph.  A list of
    /// BoxObj objects is maintained by the <see cref="GraphObjList"/> collection class.
    /// </summary>
    /// 
    /// <author> John Champion </author>
    /// <version> $Revision: 3.3 $ $Date: 2007-01-25 07:56:08 $ </version>
    [Serializable] public class BoxObj : GraphObj, ICloneable {
        #region Fields
        /// <summary>
        /// Private field that determines the properties of the border around this
        /// <see cref="BoxObj"/>
        /// Use the public property <see cref="Border"/> to access this value.
        /// </summary>
        protected Border _border;
        /// <summary>
        /// Private field that stores the <see cref="ZedGraph.Fill"/> data for this
        /// <see cref="BoxObj"/>.  Use the public property <see cref="Fill"/> to
        /// access this value.
        /// </summary>
        protected Fill _fill;
        #endregion
        #region Defaults
        /// <summary>
        /// A simple struct that defines the
        /// default property values for the <see cref="ArrowObj"/> class.
        /// </summary>
        public new struct Default {
            /// <summary>
            /// The default color used for the <see cref="BoxObj"/> border
            /// (<see cref="ZedGraph.LineBase.Color"/> property).
            /// </summary>
            public static Color BorderColor = Color.Black;
            /// <summary>
            /// The default color used for the <see cref="BoxObj"/> fill
            /// (<see cref="ZedGraph.Fill.Color"/> property).
            /// </summary>
            public static Color FillColor = Color.White;
            /// <summary>
            /// The default pen width used for the <see cref="BoxObj"/> border
            /// (<see cref="ZedGraph.LineBase.Width"/> property).  Units are points (1/72 inch).
            /// </summary>
            public static float PenWidth = 1.0F;
        }
        #endregion
        #region Properties
        /// <summary>
        /// Gets or sets the <see cref="ZedGraph.Fill"/> data for this
        /// <see cref="BoxObj"/>.
        /// </summary>
        public Fill Fill {
            get { return _fill; }
            set { _fill = value; }
        }
        /// <summary>
        /// Gets or sets the <see cref="ZedGraph.Border"/> object, which
        /// determines the properties of the border around this
        /// <see cref="BoxObj"/>
        /// </summary>
        public Border Border {
            get { return _border; }
            set { _border = value; }
        }
        #endregion
        #region Constructors
        /// <overloads>Constructors for the <see cref="BoxObj"/> object</overloads>
        /// <summary>
        /// A constructor that allows the position, border color, and solid fill color
        /// of the <see cref="BoxObj"/> to be pre-specified.
        /// </summary>
        /// <param name="borderColor">An arbitrary <see cref="System.Drawing.Color"/> specification
        /// for the box border</param>
        /// <param name="fillColor">An arbitrary <see cref="System.Drawing.Color"/> specification
        /// for the box fill (will be a solid color fill)</param>
        /// <param name="x">The x location for this <see cref="BoxObj" />.  This will be in units determined by
        /// <see cref="ZedGraph.Location.CoordinateFrame" />.</param>
        /// <param name="y">The y location for this <see cref="BoxObj" />.  This will be in units determined by
        /// <see cref="ZedGraph.Location.CoordinateFrame" />.</param>
        /// <param name="width">The width of this <see cref="BoxObj" />.  This will be in units determined by
        /// <see cref="ZedGraph.Location.CoordinateFrame" />.</param>
        /// <param name="height">The height of this <see cref="BoxObj" />.  This will be in units determined by
        /// <see cref="ZedGraph.Location.CoordinateFrame" />.</param>
        public BoxObj(double x, double y, double width, double height, Color borderColor, Color fillColor)
            : base(x, y, width, height) {
            Border = new Border(borderColor, Default.PenWidth);
            Fill = new Fill(fillColor);
        }

        /// <summary>
        /// A constructor that allows the position
        /// of the <see cref="BoxObj"/> to be pre-specified.  Other properties are defaulted.
        /// </summary>
        /// <param name="x">The x location for this <see cref="BoxObj" />.  This will be in units determined by
        /// <see cref="ZedGraph.Location.CoordinateFrame" />.</param>
        /// <param name="y">The y location for this <see cref="BoxObj" />.  This will be in units determined by
        /// <see cref="ZedGraph.Location.CoordinateFrame" />.</param>
        /// <param name="width">The width of this <see cref="BoxObj" />.  This will be in units determined by
        /// <see cref="ZedGraph.Location.CoordinateFrame" />.</param>
        /// <param name="height">The height of this <see cref="BoxObj" />.  This will be in units determined by
        /// <see cref="ZedGraph.Location.CoordinateFrame" />.</param>
        public BoxObj(double x, double y, double width, double height)
            :
                base(x, y, width, height) {
            Border = new Border(Default.BorderColor, Default.PenWidth);
            Fill = new Fill(Default.FillColor);
        }

        /// <summary>
        /// A default constructor that creates a <see cref="BoxObj"/> using a location of (0,0),
        /// and a width,height of (1,1).  Other properties are defaulted.
        /// </summary>
        public BoxObj() : this(0, 0, 1, 1) {}

        /// <summary>
        /// A constructor that allows the position, border color, and two-color
        /// gradient fill colors
        /// of the <see cref="BoxObj"/> to be pre-specified.
        /// </summary>
        /// <param name="borderColor">An arbitrary <see cref="System.Drawing.Color"/> specification
        /// for the box border</param>
        /// <param name="fillColor1">An arbitrary <see cref="System.Drawing.Color"/> specification
        /// for the start of the box gradient fill</param>
        /// <param name="fillColor2">An arbitrary <see cref="System.Drawing.Color"/> specification
        /// for the end of the box gradient fill</param>
        /// <param name="x">The x location for this <see cref="BoxObj" />.  This will be in units determined by
        /// <see cref="ZedGraph.Location.CoordinateFrame" />.</param>
        /// <param name="y">The y location for this <see cref="BoxObj" />.  This will be in units determined by
        /// <see cref="ZedGraph.Location.CoordinateFrame" />.</param>
        /// <param name="width">The width of this <see cref="BoxObj" />.  This will be in units determined by
        /// <see cref="ZedGraph.Location.CoordinateFrame" />.</param>
        /// <param name="height">The height of this <see cref="BoxObj" />.  This will be in units determined by
        /// <see cref="ZedGraph.Location.CoordinateFrame" />.</param>
        public BoxObj(
            double x,
            double y,
            double width,
            double height,
            Color borderColor,
            Color fillColor1,
            Color fillColor2) :
                base(x, y, width, height) {
            Border = new Border(borderColor, Default.PenWidth);
            Fill = new Fill(fillColor1, fillColor2);
        }

        /// <summary>
        /// The Copy Constructor
        /// </summary>
        /// <param name="rhs">The <see cref="BoxObj"/> object from which to copy</param>
        public BoxObj(BoxObj rhs) : base(rhs) {
            Border = rhs.Border.Clone();
            Fill = rhs.Fill.Clone();
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
        public BoxObj Clone() {
            return new BoxObj(this);
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
        protected BoxObj(SerializationInfo info, StreamingContext context) : base(info, context) {
            // The schema value is just a file version parameter.  You can use it to make future versions
            // backwards compatible as new member variables are added to classes
            var sch = info.GetInt32("schema2");

            _fill = (Fill) info.GetValue("fill", typeof (Fill));
            _border = (Border) info.GetValue("border", typeof (Border));
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
            info.AddValue("fill", _fill);
            info.AddValue("border", _border);
        }
        #endregion
        #region Rendering Methods
        /// <summary>
        /// Render this object to the specified <see cref="Graphics"/> device.
        /// </summary>
        /// <remarks>
        /// This method is normally only called by the Draw method
        /// of the parent <see cref="GraphObjList"/> collection object.
        /// </remarks>
        /// <param name="g">
        /// A graphic device object to be drawn into.  This is normally e.Graphics from the
        /// PaintEventArgs argument to the Paint() method.
        /// </param>
        /// <param name="pane">
        /// A reference to the <see cref="PaneBase"/> object that is the parent or
        /// owner of this object.
        /// </param>
        /// <param name="scaleFactor">
        /// The scaling factor to be used for rendering objects.  This is calculated and
        /// passed down by the parent <see cref="GraphPane"/> object using the
        /// <see cref="PaneBase.CalcScaleFactor"/> method, and is used to proportionally adjust
        /// font sizes, etc. according to the actual size of the graph.
        /// </param>
        public override void Draw(Graphics g, PaneBase pane, float scaleFactor) {
            // Convert the arrow coordinates from the user coordinate system
            // to the screen coordinate system
            var pixRect = Location.TransformRect(pane);

            // Clip the rect to just outside the PaneRect so we don't end up with wild coordinates.
            var tmpRect = pane.Rect;
            tmpRect.Inflate(20, 20);
            pixRect.Intersect(tmpRect);

            if (Math.Abs(pixRect.Left) >= 100000 || Math.Abs(pixRect.Top) >= 100000 || Math.Abs(pixRect.Right) >= 100000 ||
                Math.Abs(pixRect.Bottom) >= 100000) return;
            // If the box is to be filled, fill it
            _fill.Draw(g, pixRect);

            // Draw the border around the box if required
            _border.Draw(g, pane, scaleFactor, pixRect);
        }

        /// <summary>
        /// Determine if the specified screen point lies inside the bounding box of this
        /// <see cref="BoxObj"/>.
        /// </summary>
        /// <param name="pt">The screen point, in pixels</param>
        /// <param name="pane">
        /// A reference to the <see cref="PaneBase"/> object that is the parent or
        /// owner of this object.
        /// </param>
        /// <param name="g">
        /// A graphic device object to be drawn into.  This is normally e.Graphics from the
        /// PaintEventArgs argument to the Paint() method.
        /// </param>
        /// <param name="scaleFactor">
        /// The scaling factor to be used for rendering objects.  This is calculated and
        /// passed down by the parent <see cref="GraphPane"/> object using the
        /// <see cref="PaneBase.CalcScaleFactor"/> method, and is used to proportionally adjust
        /// font sizes, etc. according to the actual size of the graph.
        /// </param>
        /// <returns>true if the point lies in the bounding box, false otherwise</returns>
        public override bool PointInBox(PointF pt, PaneBase pane, Graphics g, float scaleFactor) {
            if (! base.PointInBox(pt, pane, g, scaleFactor)) return false;

            // transform the x,y location from the user-defined
            // coordinate frame to the screen pixel location
            var pixRect = _location.TransformRect(pane);

            return pixRect.Contains(pt);
        }

        /// <summary>
        /// Determines the shape type and Coords values for this GraphObj
        /// </summary>
        public override void GetCoords(
            PaneBase pane,
            Graphics g,
            float scaleFactor,
            out string shape,
            out string coords) {
            // transform the x,y location from the user-defined
            // coordinate frame to the screen pixel location
            var pixRect = _location.TransformRect(pane);

            shape = "rect";
            coords = String.Format(
                "{0:f0},{1:f0},{2:f0},{3:f0}",
                pixRect.Left,
                pixRect.Top,
                pixRect.Right,
                pixRect.Bottom);
        }
        #endregion
    }
}