package Messaging;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by marcleef on 11/9/15.
 * Abstract parent class for all message types.
 */
public abstract class Message {
    public abstract boolean isValid();
}
