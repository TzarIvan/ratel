using System.Runtime.Serialization;
using System.Security.Permissions;
#if ( !DOTNET1 ) // Is this a .Net 2 compilation?

#endif

namespace ZedGraph {
    /// <summary>
    /// A simple instance that stores a data point (X, Y, Z).  This differs from a regular
    /// <see cref="PointPair" /> in that it maps the <see cref="ColorValue" /> property
    /// to an independent value.  That is, <see cref="ColorValue" /> and
    /// <see cref="PointPair.Z" /> are not related (as they are in the
    /// <see cref="PointPair" />).
    /// </summary>
    public class PointPairCV : PointPair {
        #region Properties
        #endregion
        #region Constructors
        /// <summary>
        /// Creates a point pair with the specified X, Y, and base value.
        /// </summary>
        /// <param name="x">This pair's x coordinate.</param>
        /// <param name="y">This pair's y coordinate.</param>
        /// <param name="z">This pair's z or lower dependent coordinate.</param>
        public PointPairCV(double x, double y, double z)
            : base(x, y, z, null) {}
        #endregion
        #region Serialization
        /// <summary>
        /// Current schema value that defines the version of the serialized file
        /// </summary>
        public const int schema3 = 11;

        /// <summary>
        /// Constructor for deserializing objects
        /// </summary>
        /// <param name="info">A <see cref="SerializationInfo"/> instance that defines the serialized data
        /// </param>
        /// <param name="context">A <see cref="StreamingContext"/> instance that contains the serialized data
        /// </param>
        protected PointPairCV(SerializationInfo info, StreamingContext context)
            : base(info, context) {
            // The schema value is just a file version parameter.  You can use it to make future versions
            // backwards compatible as new member variables are added to classes
            var sch = info.GetInt32("schema3");

            initNoVirtualCalInConstructor(info);
        }

        void initNoVirtualCalInConstructor(SerializationInfo info) {
            ColorValue = info.GetDouble("ColorValue");
        }

        /// <summary>
        /// Populates a <see cref="SerializationInfo"/> instance with the data needed to serialize the target object
        /// </summary>
        /// <param name="info">A <see cref="SerializationInfo"/> instance that defines the serialized data</param>
        /// <param name="context">A <see cref="StreamingContext"/> instance that contains the serialized data</param>
        [SecurityPermission(SecurityAction.Demand, SerializationFormatter = true)] public override void GetObjectData(
            SerializationInfo info, StreamingContext context) {
            base.GetObjectData(info, context);
            info.AddValue("schema3", schema2);
            info.AddValue("ColorValue", ColorValue);
        }
        #endregion
        #region Properties
        /// <summary>
        /// The ColorValue property.  This is used with the
        /// <see cref="FillType.GradientByColorValue" /> option.
        /// </summary>
        public override double ColorValue { get; set; }
        #endregion
    }
}