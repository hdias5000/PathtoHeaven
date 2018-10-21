
//This class is used as a custom listener on variables to be passed onto other classes//

package com.example.jay1805.itproject;

public interface OnEventListener<T> {
    public void onSuccess(T object);
    public void onFailure(Exception e);
}
