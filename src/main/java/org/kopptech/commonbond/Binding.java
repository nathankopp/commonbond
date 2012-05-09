package org.kopptech.commonbond;

import java.lang.reflect.Type;

public interface Binding
{
    public Object getValue(boolean createParentIfNull);
    public Type getType();
    public void setValue(Object o, boolean allowInvalid);
    public Object createDefaultValue();
    public boolean isAllowConstraintViolations();
    public void setAllowConstraintViolations(boolean allowConstraintViolations);
}
