package org.kopptech.commonbond;

import org.kopptech.commonbond.bindings.DummyBinding;
import org.kopptech.commonbond.bindings.ElBinding;
import org.kopptech.commonbond.bindings.FieldBinding;
import org.kopptech.commonbond.bindings.GetterSetterBinding;
import org.kopptech.commonbond.bindings.OgnlBinding;

public enum BindType {
    AUTO {
        @Override
        public Binding createBinding(Binding parent, String fieldName, Class<?> instanceType)
        {
            return new FieldBinding(parent, fieldName, instanceType);
        }
    },
    FIELD {
        @Override
        public Binding createBinding(Binding parent, String fieldName, Class<?> instanceType)
        {
            return new FieldBinding(parent, fieldName, instanceType);
        }
    },
    METHOD {
        @Override
        public Binding createBinding(Binding parent, String fieldName, Class<?> instanceType)
        {
            return new GetterSetterBinding(parent, fieldName, instanceType);
        }
    },
    OGNL {
        @Override
        public Binding createBinding(Binding parent, String fieldName, Class<?> instanceType)
        {
            return new OgnlBinding(parent, fieldName, instanceType);
        }
    },
    EL {
        @Override
        public Binding createBinding(Binding parent, String fieldName, Class<?> instanceType)
        {
            return new ElBinding(parent, fieldName, instanceType);
        }
    },
    DUMMY {
        @Override
        public Binding createBinding(Binding parent, String fieldName, Class<?> instanceType)
        {
            return new DummyBinding(parent);
        }
    };;

    public abstract Binding createBinding(Binding parent, String fieldName, Class<?> instanceType);
}
