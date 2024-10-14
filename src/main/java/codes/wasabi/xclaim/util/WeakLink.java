package codes.wasabi.xclaim.util;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import java.lang.ref.PhantomReference;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.lang.reflect.Method;

/**
 * Allows us to hold a reference to an object without preventing that object from being garbage collected.
 * As a result, this is impossible to dereference. It is, however, possible to check if an object is the referent
 * of this link.
 */
public class WeakLink<T> {

    private static final Method M_REFERS_TO;
    private static final boolean M_REFERS_TO_SUPPORT;
    private static final Method M_EQUALS;
    private static final boolean M_EQUALS_SUPPORT;
    static {
        Method m = null;
        try {
            m = PhantomReference.class.getMethod("refersTo", Object.class);
        } catch (NoSuchMethodException ignored) { }
        M_REFERS_TO = m;
        M_REFERS_TO_SUPPORT = (m != null);

        m = null;
        try {
            m = Object.class.getMethod("equals", Object.class);
        } catch (NoSuchMethodException ignored) { }
        M_EQUALS = m;
        M_EQUALS_SUPPORT = (m != null);
    }

    protected Reference<T> reference = null;
    public WeakLink(@Nullable T initialValue) {
        if (initialValue != null) this.set(initialValue);
    }

    public WeakLink() {
        this(null);
    }

    public void set(@Nullable T value) {
        if (this.reference != null) this.reference.clear();
        if (value == null) {
            this.reference = null;
            return;
        }
        if (M_REFERS_TO_SUPPORT) {
            this.reference = new PhantomReference<>(value, null);
        } else {
            this.reference = new WeakReference<>(value);
        }
    }

    @Contract("null -> false")
    public boolean refersTo(@Nullable T referent) {
        if (referent == null || this.reference == null) return false;

        if (M_REFERS_TO_SUPPORT) {
            try {
                return (Boolean) M_REFERS_TO.invoke(this.reference, referent);
            } catch (ReflectiveOperationException e) {
                throw new RuntimeException(e);
            }
        }

        final Object value = this.reference.get();
        if (M_EQUALS_SUPPORT) {
            try {
                return (Boolean) M_EQUALS.invoke(referent, value);
            } catch (ReflectiveOperationException e) {
                throw new RuntimeException(e);
            }
        }
        return referent.equals(value);
    }

}
