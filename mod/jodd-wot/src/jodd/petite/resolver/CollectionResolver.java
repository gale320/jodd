// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.petite.resolver;

import jodd.introspector.ClassDescriptor;
import jodd.introspector.ClassIntrospector;
import jodd.petite.InjectionPointFactory;
import jodd.petite.CollectionInjectionPoint;
import jodd.petite.meta.PetiteInject;
import jodd.util.ReflectUtil;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CollectionResolver {

	protected final Map<Class, CollectionInjectionPoint[]> collections = new HashMap<Class, CollectionInjectionPoint[]>();

	protected final InjectionPointFactory injectionPointFactory;

	public CollectionResolver(InjectionPointFactory injectionPointFactory) {
		this.injectionPointFactory = injectionPointFactory;
	}

	/**
	 * Resolves all collections for given type.
	 */
	public CollectionInjectionPoint[] resolve(Class type, boolean autowire) {
		CollectionInjectionPoint[] fields = collections.get(type);
		if (fields != null) {
			return fields;
		}

		// lookup fields
		ClassDescriptor cd = ClassIntrospector.lookup(type);
		List<CollectionInjectionPoint> list = new ArrayList<CollectionInjectionPoint>();
		Field[] allFields = cd.getAllFields(true);
		for (Field field : allFields) {
			PetiteInject ref = field.getAnnotation(PetiteInject.class);
			if ((autowire == false) && (ref == null)) {
				continue;
			}

			if (ReflectUtil.isSubclass(field.getType(), Collection.class) == false) {
				continue;
			}

			list.add(injectionPointFactory.createCollectionInjectionPoint(field));
		}
		if (list.isEmpty()) {
			fields = CollectionInjectionPoint.EMPTY;
		} else {
			fields = list.toArray(new CollectionInjectionPoint[list.size()]);
		}
		collections.put(type, fields);
		return fields;
	}

	public void remove(Class type) {
		collections.remove(type);
	}

}