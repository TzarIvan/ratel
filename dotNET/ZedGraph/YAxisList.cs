#region Using directives
using System;
using System.Collections.Generic;

#endregion

namespace ZedGraph {
    /// <summary>
    /// A collection class containing a list of <see cref="YAxis"/> objects.
    /// </summary>
    /// 
    /// <author>John Champion</author>
    /// <version> $Revision: 3.3 $ $Date: 2006-06-24 20:26:43 $ </version>
    [Serializable] public class YAxisList : List<YAxis>, ICloneable {
        #region Constructors
        /// <summary>
        /// Default constructor for the collection class.
        /// </summary>
        public YAxisList() {}

        /// <summary>
        /// The Copy Constructor
        /// </summary>
        /// <param name="rhs">The <see cref="YAxisList"/> object from which to copy</param>
        public YAxisList(IEnumerable<YAxis> rhs) {
            foreach (var item in rhs) Add(item.Clone());
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
        public YAxisList Clone() {
            return new YAxisList(this);
        }
        #endregion
        #region List Methods
        /// <summary>
        /// Indexer to access the specified <see cref="Axis"/> object by
        /// its ordinal position in the list.
        /// </summary>
        /// <param name="index">The ordinal position (zero-based) of the
        /// <see cref="YAxis"/> object to be accessed.</param>
        /// <value>An <see cref="Axis"/> object reference.</value>
        public new YAxis this[int index] {
            get { return (((index < 0 || index >= Count) ? null : base[index])); }
        }

        /// <summary>
        /// Indexer to access the specified <see cref="Axis"/> object by
        /// its <see cref="Axis.Title"/> string.
        /// </summary>
        /// <param name="title">The string title of the
        /// <see cref="YAxis"/> object to be accessed.</param>
        /// <value>A <see cref="Axis"/> object reference.</value>
        public YAxis this[string title] {
            get {
                var index = IndexOf(title);
                return index >= 0 ? this[index] : null;
            }
        }

        /// <summary>
        /// Return the zero-based position index of the
        /// <see cref="Axis"/> with the specified <see cref="Axis.Title"/>.
        /// </summary>
        /// <remarks>The comparison of titles is not case sensitive, but it must include
        /// all characters including punctuation, spaces, etc.</remarks>
        /// <param name="title">The <see cref="String"/> label that is in the
        /// <see cref="Axis.Title"/> attribute of the item to be found.
        /// </param>
        /// <returns>The zero-based index of the specified <see cref="Axis"/>,
        /// or -1 if the <see cref="Axis.Title"/> was not found in the list</returns>
        /// <seealso cref="IndexOfTag"/>
        public int IndexOf(string title) {
            var index = 0;
            foreach (var axis in this) {
                if (String.Compare(axis.Title._text, title, true) == 0) return index;
                index++;
            }

            return -1;
        }

        /// <summary>
        /// Return the zero-based position index of the
        /// <see cref="Axis"/> with the specified <see cref="Axis.Tag" />.
        /// </summary>
        /// <remarks>In order for this method to work, the <see cref="Axis.Tag" />
        /// property must be of type <see cref="String"/>.
        /// </remarks>
        /// <param name="tagStr">The <see cref="String"/> tag that is in the
        /// <see cref="Axis.Tag" /> attribute of the item to be found.
        /// </param>
        /// <returns>The zero-based index of the specified <see cref="Axis" />,
        /// or -1 if the <see cref="Axis.Tag" /> string is not in the list</returns>
        public int IndexOfTag(string tagStr) {
            var index = 0;
            foreach (var axis in this) {
                if (axis.Tag is string &&
                    String.Compare((string) axis.Tag, tagStr, true) == 0) return index;
                index++;
            }

            return -1;
        }

        /// <summary>
        /// Create a new <see cref="YAxis" /> and add it to this list.
        /// </summary>
        /// <param name="title">The title string for the new axis</param>
        /// <returns>An integer representing the ordinal position of the new <see cref="YAxis" /> in
        /// this <see cref="YAxisList" />.  This is the value that you would set the
        /// <see cref="CurveItem.YAxisIndex" /> property of a given <see cref="CurveItem" /> to 
        /// assign it to this new <see cref="YAxis" />.</returns>
        public int Add(string title) {
            var axis = new YAxis(title);
            Add(axis);

            return Count - 1;
        }
        #endregion
    }
}