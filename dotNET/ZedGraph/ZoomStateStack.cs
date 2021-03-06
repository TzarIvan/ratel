#region Using directives
using System;
using System.Collections.Generic;

#endregion

namespace ZedGraph {
    /// <summary>
    /// A LIFO stack of prior <see cref="ZoomState"/> objects, used to allow zooming out to prior
    /// states (of scale range settings).
    /// </summary>
    /// <author> John Champion </author>
    /// <version> $Revision: 3.1 $ $Date: 2006-06-24 20:26:44 $ </version>
    public class ZoomStateStack : List<ZoomState>, ICloneable {
        /// <summary>
        /// Default Constructor
        /// </summary>
        public ZoomStateStack() {}

        /// <summary>
        /// The Copy Constructor
        /// </summary>
        /// <param name="rhs">The <see cref="ZoomStateStack"/> object from which to copy</param>
        public ZoomStateStack(IEnumerable<ZoomState> rhs) {
            foreach (var state in rhs) Add(new ZoomState(state));
        }

        /// <summary>
        /// Public readonly property that indicates if the stack is empty
        /// </summary>
        /// <value>true for an empty stack, false otherwise</value>
        public bool IsEmpty {
            get { return Count == 0; }
        }
        /// <summary>
        /// Gets a reference to the <see cref="ZoomState"/> object at the top of the stack,
        /// without actually removing it from the stack.
        /// </summary>
        /// <value>A <see cref="ZoomState"/> object reference, or null if the stack is empty.</value>
        public ZoomState Top {
            get {
                return !IsEmpty ? this[Count - 1] : null;
            }
        }
        #region ICloneable Members
        /// <summary>
        /// Implement the <see cref="ICloneable" /> interface in a typesafe manner by just
        /// calling the typed version of <see cref="Clone" />
        /// </summary>
        /// <returns>A deep copy of this object</returns>
        object ICloneable.Clone() {
            return Clone();
        }
        #endregion
        /// <summary>
        /// Typesafe, deep-copy clone method.
        /// </summary>
        /// <returns>A new, independent copy of this class</returns>
        public ZoomStateStack Clone() {
            return new ZoomStateStack(this);
        }

        /// <summary>
        /// Add the scale range information from the specified <see cref="GraphPane"/> object as a
        /// new <see cref="ZoomState"/> entry on the stack.
        /// </summary>
        /// <param name="pane">The <see cref="GraphPane"/> object from which the scale range
        /// information should be copied.</param>
        /// <param name="type">A <see cref="ZoomState.StateType"/> enumeration that indicates whether this
        /// state is the result of a zoom or pan operation.</param>
        /// <returns>The resultant <see cref="ZoomState"/> object that was pushed on the stack.</returns>
        public ZoomState Push(GraphPane pane, ZoomState.StateType type) {
            var state = new ZoomState(pane, type);
            Add(state);
            return state;
        }

        /// <summary>
        /// Add the scale range information from the specified <see cref="ZoomState"/> object as a
        /// new <see cref="ZoomState"/> entry on the stack.
        /// </summary>
        /// <param name="state">The <see cref="ZoomState"/> object to be placed on the stack.</param>
        /// <returns>The <see cref="ZoomState"/> object (same as the <see paramref="state"/>
        /// parameter).</returns>
        public ZoomState Push(ZoomState state) {
            Add(state);
            return state;
        }

        /// <summary>
        /// Pop a <see cref="ZoomState"/> entry from the top of the stack, and apply the properties
        /// to the specified <see cref="GraphPane"/> object.
        /// </summary>
        /// <param name="pane">The <see cref="GraphPane"/> object to which the scale range
        /// information should be copied.</param>
        /// <returns>The <see cref="ZoomState"/> object that was "popped" from the stack and applied
        /// to the specified <see cref="GraphPane"/>.  null if no <see cref="ZoomState"/> was
        /// available (the stack was empty).</returns>
        public ZoomState Pop(GraphPane pane) {
            if (IsEmpty) return null;
            var state = this[Count - 1];
            RemoveAt(Count - 1);

            state.ApplyState(pane);
            return state;
        }

        /// <summary>
        /// Pop the <see cref="ZoomState"/> entry from the bottom of the stack, and apply the properties
        /// to the specified <see cref="GraphPane"/> object.  Clear the stack completely.
        /// </summary>
        /// <param name="pane">The <see cref="GraphPane"/> object to which the scale range
        /// information should be copied.</param>
        /// <returns>The <see cref="ZoomState"/> object at the bottom of the stack that was applied
        /// to the specified <see cref="GraphPane"/>.  null if no <see cref="ZoomState"/> was
        /// available (the stack was empty).</returns>
        public ZoomState PopAll(GraphPane pane) {
            if (IsEmpty) return null;
            var state = this[0];
            Clear();

            state.ApplyState(pane);

            return state;
        }
    }
}