package at.bestsolution.javafx.ide.editor.js;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import netscape.javascript.JSObject;

public abstract class ProxyNCICallback<O> implements JavaScriptNCICallback<JSObject> {
	private Class<O> jInterface;
	
	public ProxyNCICallback(Class<O> jInterface) {
		this.jInterface = jInterface;
	}
	
	@Override
	public void initJava(JSObject jsObject) {
		O o = (O) Proxy.newProxyInstance(jInterface.getClassLoader(), new Class[] {jInterface}, new JsInvocationHandler(jsObject, jInterface));
		init(o);
	}
	
	protected abstract void init(O o);
	
	public static class JsInvocationHandler<O> implements InvocationHandler {
		private JSObject jsObject;
		private Class<O> jInterface;
		
		public JsInvocationHandler(JSObject jsObject, Class<O> jInterface) {
			this.jsObject = jsObject;
			this.jInterface = jInterface;
		}
		
		@Override
		public Object invoke(Object proxy, Method method, Object[] args)
				throws Throwable {
			if( method.getDeclaringClass() != Object.class ) {
				jsObject.call(method.getName(), args);
			} else if( method.getName().equals("toString") ) {
				return "proxy " + jInterface.getName() + "(" + jsObject + ")";
			} else  {
				throw new UnsupportedOperationException("The method '"+method.getName()+"' is not supported.");
			}
			
			return null;
		}
		
	}
}
