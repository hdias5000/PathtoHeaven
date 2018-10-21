/**TEAM PATH TO HEAVEN
 * Authors:
 *  - Hasitha Dias:   789929
 *  - Jay Parikh:     864675
 *  - Anupama Sodhi:  791288
 *  - Kushagra Gupta: 804729
 *  - Manindra Arora: 827703
 * **/

//This class is used as a custom listener on variables to be passed onto other classes//

package com.example.jay1805.itproject;

public interface OnEventListener<T> {
    public void onSuccess(T object);
    public void onFailure(Exception e);
}
