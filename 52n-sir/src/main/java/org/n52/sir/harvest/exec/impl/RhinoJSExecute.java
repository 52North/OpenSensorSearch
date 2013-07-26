package org.n52.sir.harvest.exec.impl;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.n52.sir.harvest.exec.IJSExecute;

public class RhinoJSExecute implements IJSExecute {

	@Override
	public String execute(String s) {
		// TODO Auto-generated method stub
		Context cn = Context.enter();
		try{
			Scriptable scope = cn.initStandardObjects();
			Object result = cn.evaluateString(scope, s, "<cmd>",1,null);
			return Context.toString(result);
		}catch(Exception e){
			return null;
		}finally{
			cn.exit();
		}
	}
	

}
