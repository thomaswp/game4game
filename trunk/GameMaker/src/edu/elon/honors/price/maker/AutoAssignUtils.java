package edu.elon.honors.price.maker;

import java.lang.reflect.Field;

import android.view.View;
import edu.elon.honors.price.game.Debug;

public class AutoAssignUtils {
	/**
	 * An experimental (but stable) method that
	 * assigns any fields in this class marked with
	 * the {@link AutoAssign} annotation to the View
	 * whose id's name is equal to either that field's name
	 * or the name property of its AutoAssign annotation.
	 * Alternatively, you can mark the whole class with the
	 * AutoAssign annotation and any viable fields will be
	 * auto-assigned.
	 * <br /> <br />
	 * For example, if there is a field in this class defined as:
	 * 
	 * <p><code>
	 * &#64;AutoAssign private EditText editTextExample;
	 * </code></p>
	 * 
	 * and there is an EditText being displayed that you would
	 * normally retrieve with:
	 * 
	 * <p><code>
	 * editTextExample = findViewById(R.id.editTextExample);
	 * </code></p>
	 * 
	 * this method would make that assignment for you 
	 * automatically using reflection. Alternately, you can
	 * name the variable whatever you want, as long as you
	 * give the name of the View's id to the 
	 * Autoassign annotation. For example:
	 * 
	 * <p><code>
	 * &#64;AutoAssign(name = "editTextExample") <br />
	 * EditText myEditText;
	 * </code></p>
	 * 
	 * will work the same way as the previous example.
	 */
	public static void autoAssign(IViewContainer viewContainer) {
		Class<?> c = viewContainer.getClass();
		
		Field[] fields = c.getDeclaredFields();
		boolean typeAnnotation = 
			c.getAnnotation(AutoAssign.class) != null;
		for (Field field : fields) {
			if (!View.class.isAssignableFrom(field.getType())) {
				continue;
			}
			AutoAssign aa = field.getAnnotation(AutoAssign.class);
			if (aa != null || typeAnnotation) {
				String name;
				if (aa != null && aa.name().length() > 0) {
					name = aa.name();
				} else {
					name = field.getName();
				}
				
				try {
					Field id = R.id.class.getField(name);
					int lid = (Integer)id.get(null);
					View view = viewContainer.findViewById(lid);
					if (view == null) {
						Debug.write("No view with id %s in conent view", name);
						continue;
					}
					if (!field.getType().isAssignableFrom(view.getClass())) {
						Debug.write("Cannot assign view of type %s to %s of type %s",
								view.getClass().getName(),
								field.getName(),
								field.getType().getName());
						continue;
					}
					field.setAccessible(true);
					field.set(viewContainer, view);
					//Debug.write("%s successfully assigned!", field.getName());
				} catch (Exception e) {
					Debug.write("Problem with AutoAssign for field %s in class %s",
							field.getName(), c.getName());
					Debug.write(e);
				}
			}
		}
	}
}
