/**
 * ﻿Copyright (C) 2012 52°North Initiative for Geospatial Open Source Software GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.n52.sir.harvest.exec.impl;

import java.io.File;
import java.io.FileReader;

import org.mozilla.javascript.ClassShutter;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.tools.shell.Global;
import org.n52.sir.harvest.exec.IJSExecute;

public class RhinoJSExecute implements IJSExecute {
    
	public RhinoJSExecute() {
	    //
	}

	public RhinoJSExecute(String s) {
	    // FIXME implement method?!
	}

	@Override
	public String execute(String s) {
		Context cn = Context.enter();
		try {
			Global global = new Global();
			global.init(cn);
			Scriptable scope = cn.initStandardObjects(global);
			cn.setOptimizationLevel(-1);
			Object result = cn.evaluateString(scope, s, "<cmd>", 1, null);
			return Context.toString(result);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			Context.exit();
		}
	}

	@Override
	public String execute(File f) {
		Context cn = ContextFactory.getGlobal().enter();
		try {
			Global global = new Global();
			global.init(cn);
			Scriptable scope = cn.initStandardObjects(global);
			cn.setOptimizationLevel(-1);
			FileReader reader = new FileReader(f);
			Object result = cn.evaluateReader(scope, reader, "<cmd>", 1, null);
			reader.close();
			return Context.toString(result);
			
		} catch (Exception e) {
			System.out.println(e);
			e.printStackTrace();
			return null;
		} finally {
			Context.exit();
		}
	}

	private void setClassShutter(Context c) {
		c.setClassShutter(new ClassShutter() {

			@Override
			public boolean visibleToScripts(String arg0) {
				if (arg0.startsWith("org.n52")) {
					for (String s : RhinoConstants.allowed) {
						if (arg0.contains(s))
							return true;
					}
					return false;
					// TODO modify the security to be more aware of suspicious
					// methods and classes
				} else
					return true;
			}
		});

	}
}
