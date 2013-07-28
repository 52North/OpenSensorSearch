package org.n52.sir.harvest.exec.impl;

import java.io.File;
import java.io.FileReader;

import org.mozilla.javascript.ClassShutter;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.n52.sir.harvest.exec.IJSExecute;

public class RhinoJSExecute implements IJSExecute {

	public RhinoJSExecute() {

	}

	public RhinoJSExecute(String s) {

	}

	@Override
	public String execute(String s) {
		Context cn = Context.enter();
		try {
			Scriptable scope = cn.initStandardObjects();
			Object result = cn.evaluateString(scope, s, "<cmd>", 1, null);
			return Context.toString(result);
		} catch (Exception e) {
			return null;
		} finally {
			cn.exit();
		}
	}

	@Override
	public String execute(File f) {
		Context cn = Context.enter();
		try {
			Scriptable scope = cn.initStandardObjects();
			FileReader reader = new FileReader(f);
			Object result = cn.evaluateReader(scope, reader, "<cmd>", 1, null);
			reader.close();
			return Context.toString(result);
		} catch (Exception e) {
			return null;
		} finally {
			cn.exit();
		}
	}

	private void setClassShutter(Context c) {
		c.setClassShutter(new ClassShutter() {

			@Override
			public boolean visibleToScripts(String arg0) {
				if (arg0.startsWith("org.n52")) {
					for (String s : RhinoConstants.allowed) {
						if(arg0.contains(s))return true;
					}
					return false;
					//TODO modify the security to be more aware of suspicious methods and classes
				}else return true;
			}
		});

	}
}
