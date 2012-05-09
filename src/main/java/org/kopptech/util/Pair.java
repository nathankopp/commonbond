/*******************************************************************************
 * Copyright 2010 Nathan Kopp
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package org.kopptech.util;

public class Pair<T1, T2>
{
    private T1 a;
    private T2 b;
    
    public Pair(T1 a, T2 b)
    {
        super();
        this.a = a;
        this.b = b;
    }
    
    public T1 getA()
    {
        return a;
    }
    public void setA(T1 a)
    {
        this.a = a;
    }
    public T2 getB()
    {
        return b;
    }
    public void setB(T2 b)
    {
        this.b = b;
    }
}
