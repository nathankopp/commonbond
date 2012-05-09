package org.kopptech.commonbond;

import java.net.URL;
import java.util.List;


public interface BindingReader
{
    BindingNode readListBinding(URL url, List<?> obj, Class<?> newInstanceClass) throws Exception;
    BindingNode readNonListBinding(URL url, Object obj) throws Exception;
}
