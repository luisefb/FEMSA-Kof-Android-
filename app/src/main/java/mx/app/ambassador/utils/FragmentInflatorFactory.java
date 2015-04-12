package mx.app.ambassador.utils;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class FragmentInflatorFactory implements LayoutInflater.Factory {

    private static final int[] sWantedAttrs = { android.R.attr.onClick };

    private static final Method sOnCreateViewMethod;
    static {
        // We could duplicate its functionallity.. or just ignore its a protected method.
        try {
            Method method = LayoutInflater.class.getDeclaredMethod("onCreateView", String.class, AttributeSet.class);
            method.setAccessible(true);
            sOnCreateViewMethod = method;
        } catch (NoSuchMethodException e) {
            // Public API: Should not happen.
            throw new RuntimeException(e);
        }
    }

    private final LayoutInflater mInflator;
    private final Object mFragment;

    public FragmentInflatorFactory(LayoutInflater delegate, Object fragment) {
        if (delegate == null || fragment == null) {
            throw new NullPointerException();
        }
        mInflator = delegate;
        mFragment = fragment;
    }

    public static LayoutInflater inflatorFor(LayoutInflater original, Object fragment) {
        LayoutInflater inflator = original.cloneInContext(original.getContext());
        FragmentInflatorFactory factory = new FragmentInflatorFactory(inflator, fragment);
        inflator.setFactory(factory);
        return inflator;
    }

    @Override
    public View onCreateView(String name, Context context, AttributeSet attrs) {
        if ("fragment".equals(name)) {
            // Let the Activity ("private factory") handle it
            return null;
        }

        View view = null;

        if (name.indexOf('.') == -1) {
            try {
                view = (View) sOnCreateViewMethod.invoke(mInflator, name, attrs);
            } catch (IllegalAccessException e) {
                throw new AssertionError(e);
            } catch (InvocationTargetException e) {
                if (e.getCause() instanceof ClassNotFoundException) {
                    return null;
                }
                throw new RuntimeException(e);
            }
        } else {
            try {
                view = mInflator.createView(name, null, attrs);
            } catch (ClassNotFoundException e) {
                return null;
            }
        }

        TypedArray a = context.obtainStyledAttributes(attrs, sWantedAttrs);
        String methodName = a.getString(0);
        a.recycle();

        if (methodName != null) {
            view.setOnClickListener(new FragmentClickListener(mFragment, methodName));
        }
        return view;
    }

    private static class FragmentClickListener implements OnClickListener {

        private final Object mFragment;
        private final String mMethodName;
        private Method mMethod;

        public FragmentClickListener(Object fragment, String methodName) {
            mFragment = fragment;
            mMethodName = methodName;
        }

        @Override
        public void onClick(View v) {
        	        	
            if (mMethod == null) {
                Class<?> clazz = mFragment.getClass();
                try {
                    mMethod = clazz.getMethod(mMethodName, View.class);
                } catch (NoSuchMethodException e) {
                    throw new IllegalStateException(
                            "Cannot find public method " + mMethodName + "(View) on "
                                    + clazz + " for onClick");
                }
            }

            try {
                mMethod.invoke(mFragment, v);
            } catch (InvocationTargetException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new AssertionError(e);
            }
        }
    }
}