/*
 * Copyright 2016 Crown Copyright
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

package stroom.cache.client.presenter;

import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.View;
import stroom.cache.shared.CacheRow;
import stroom.content.client.presenter.ContentTabPresenter;
import stroom.dispatch.client.ClientDispatchAsync;
import stroom.widget.button.client.GlyphIcons;
import stroom.widget.tab.client.presenter.Icon;

public class CachePresenter extends ContentTabPresenter<CachePresenter.CacheView> {
    public static final String LIST = "LIST";
    public static final String NODE_LIST = "NODE_LIST";

    public interface CacheView extends View {
    }

    private final CacheListPresenter cacheListPresenter;
    private final CacheNodeListPresenter cacheNodeListPresenter;

    @Inject
    public CachePresenter(final EventBus eventBus, final CacheView view, final CacheListPresenter cacheListPresenter,
                          final CacheNodeListPresenter cacheNodeListPresenter, final ClientDispatchAsync dispatcher) {
        super(eventBus, view);
        this.cacheListPresenter = cacheListPresenter;
        this.cacheNodeListPresenter = cacheNodeListPresenter;

        setInSlot(LIST, cacheListPresenter);
        setInSlot(NODE_LIST, cacheNodeListPresenter);
    }

    @Override
    protected void onBind() {
        super.onBind();

        registerHandler(
                cacheListPresenter.getSelectionModel().addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
                    @Override
                    public void onSelectionChange(final SelectionChangeEvent event) {
                        final CacheRow row = cacheListPresenter.getSelectionModel().getSelectedObject();
                        cacheNodeListPresenter.read(row);
                    }
                }));
    }

    @Override
    public Icon getIcon() {
        return GlyphIcons.MONITORING;
    }

    @Override
    public String getLabel() {
        return "Caches";
    }
}
