/*
 * Copyright 2013 52°North Initiative for Geospatial Open Source Software GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/**
 * @author thatcher
 */
var starttime = new Date().getTime(),
    endtime,
    q$;

Envjs({
    //let it load the script from the html
    scriptTypes: {
        "text/javascript"   :true
    },
    afterScriptLoad:{
        'data/testrunner.js': function(){
            //console.log('loaded test runner');
            //hook into qunit.log
            var count = 0,
                module;
            
            
            try{
            //jquery/qunit 1.3.x - current
                QUnit.moduleStart = function(name, testEnvironment) {
                    module = name;
                };
                QUnit.log = function(result, message){
                    console.log('{'+module+'}(' + (count++) + ')[' + 
                        ((!!result) ? 'PASS' : 'FAIL') + '] ' + message);
                };
                //hook into qunit.done
                QUnit.done = function(fail, pass){
                    endtime = new Date().getTime();
                    console.log('\n\tRESULTS: ( of '+(pass+fail)+' total tests )');
                    console.log('\t\tPASSED: ' +pass);
                    console.log('\t\tFAILED: ' +fail);
                    console.log('\tCompleted in '+(endtime-starttime)+' milliseconds.\n');
                    
                    console.log('Writing Results to File');
                    jQuery('script').each(function(){
                        this.type = 'text/envjs';
                    });
                    Envjs.writeToFile(
                        document.documentElement.outerHTML, 
                        Envjs.uri('results.html')
                    );
                };
                
                //allow jquery to run ajax
                isLocal = false;
                jQuery.ajaxSetup({async : false});
                
                //we are breaking becuase our cirucular dom referencing 
                //causes infinite recursion somewhere in jsDump;
                QUnit.jsDump = {
                    parse: function(thing){
                        return thing+"";
                    }
                }
                
                //we know some ajax calls will fail becuase
                //we are not running against a running server
                //for php files
                var handleError = jQuery.handleError;
                jQuery.handleError = function(){
                    ok(false, 'Ajax may have failed while running locally');
                    try{
                        handleError(arguments);
                    }catch(e){
                        console.log(e);
                    }
                    //allow tests to gracefully continue
                    start();
                };
                //allow unanticipated xhr error with no ajax.handleError 
                //callback (eg jQuery.getScript) to exit gracefully
                Envjs.onInterrupt = function(){
                    console.log('thread interupt: gracefully continuing test');
                    start();
                };
                
               
                Envjs.onScriptLoadError = function(script, e){
                    console.log("failed to load script %s %s", script.src, script.text, e);    
                    ok(false, 'Ajax may have failed to load correct script while running locally');
                    //allow tests to gracefully continue
                    start();
                };
                
            }catch(e){
                //jquery 1.2.x 
                console.log('qunit pre');
                q$ = _config.Test.push;
                _config.Test.push = function(){
                    try{
                        console.log("[%s] %s", arguments[0][0], arguments[0][1]);
                    }catch(e){
                        console.log("[error] %s", e);
                    }finally{
                        return Array.prototype.push.apply(_config.Test, arguments);
                    }
                };
            }
        }
    }
});

