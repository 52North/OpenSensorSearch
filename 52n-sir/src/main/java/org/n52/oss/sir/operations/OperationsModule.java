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

package org.n52.oss.sir.operations;

import org.n52.sir.SirConstants;
import org.n52.sir.listener.ConnectToCatalogListener;
import org.n52.sir.listener.DeleteSensorInfoListener;
import org.n52.sir.listener.DescribeSensorListener;
import org.n52.sir.listener.DisconnectFromCatalogListener;
import org.n52.sir.listener.GetCapabilitiesListener;
import org.n52.sir.listener.GetSensorStatusListener;
import org.n52.sir.listener.HarvestServiceListener;
import org.n52.sir.listener.ISirRequestListener;
import org.n52.sir.listener.InsertSensorInfoListener;
import org.n52.sir.listener.InsertSensorStatusListener;
import org.n52.sir.listener.SearchSensorListener;
import org.n52.sir.listener.SubscribeSensorStatusListener;
import org.n52.sir.listener.UpdateSensorDescriptionListener;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.MapBinder;

/**
 * using
 * http://google-guice.googlecode.com/svn/trunk/latest-javadoc/com/google/inject/multibindings/MapBinder.html
 * 
 * @author Daniel Nüst
 * 
 */
public class OperationsModule extends AbstractModule {

    @Override
    protected void configure() {
        MapBinder<String, ISirRequestListener> mapbinder = MapBinder.newMapBinder(binder(),
                                                                                  String.class,
                                                                                  ISirRequestListener.class);
        mapbinder.addBinding(SirConstants.Operations.GetCapabilities.name()).to(GetCapabilitiesListener.class);
        mapbinder.addBinding(SirConstants.Operations.DeleteSensorInfo.name()).to(DeleteSensorInfoListener.class);
        mapbinder.addBinding(SirConstants.Operations.DescribeSensor.name()).to(DescribeSensorListener.class);
        mapbinder.addBinding(SirConstants.Operations.ConnectToCatalog.name()).to(ConnectToCatalogListener.class);
        mapbinder.addBinding(SirConstants.Operations.DisconnectFromCatalog.name()).to(DisconnectFromCatalogListener.class);
        mapbinder.addBinding(SirConstants.Operations.GetSensorStatus.name()).to(GetSensorStatusListener.class);
        mapbinder.addBinding(SirConstants.Operations.HarvestService.name()).to(HarvestServiceListener.class);
        mapbinder.addBinding(SirConstants.Operations.InsertSensorInfo.name()).to(InsertSensorInfoListener.class);
        mapbinder.addBinding(SirConstants.Operations.InsertSensorStatus.name()).to(InsertSensorStatusListener.class);
        mapbinder.addBinding(SirConstants.Operations.SearchSensor.name()).to(SearchSensorListener.class);
        mapbinder.addBinding(SirConstants.Operations.SubscribeSensorStatus.name()).to(SubscribeSensorStatusListener.class);
        mapbinder.addBinding(SirConstants.Operations.UpdateSensorDescription.name()).to(UpdateSensorDescriptionListener.class);
    }

}
