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

package stroom.security.client.presenter;

import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.MyPresenterWidget;
import com.gwtplatform.mvp.client.View;
import stroom.security.shared.*;
import stroom.widget.button.client.GlyphButtonView;
import stroom.widget.button.client.GlyphIcons;
import stroom.widget.popup.client.event.HidePopupEvent;
import stroom.widget.popup.client.event.ShowPopupEvent;
import stroom.widget.popup.client.presenter.PopupSize;
import stroom.widget.popup.client.presenter.PopupUiHandlers;
import stroom.widget.popup.client.presenter.PopupView;

import javax.inject.Inject;
import javax.inject.Provider;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DocumentPermissionsTabPresenter
        extends MyPresenterWidget<DocumentPermissionsTabPresenter.DocumentPermissionsTabView> {
    private final DocumentUserListPresenter userListPresenter;
    private final PermissionsListPresenter permissionsListPresenter;
    private final Provider<AdvancedUserListPresenter> selectUserPresenterProvider;
    private final GlyphButtonView addButton;
    private final GlyphButtonView removeButton;

    private DocumentPermissions documentPermissions;
    private boolean group;
    private ChangeSet<UserPermission> changeSet;

    @Inject
    public DocumentPermissionsTabPresenter(final EventBus eventBus, final DocumentPermissionsTabView view,
                                           final DocumentUserListPresenter userListPresenter, final PermissionsListPresenter permissionsListPresenter,
                                           final Provider<AdvancedUserListPresenter> selectUserPresenterProvider) {
        super(eventBus, view);
        this.userListPresenter = userListPresenter;
        this.permissionsListPresenter = permissionsListPresenter;
        this.selectUserPresenterProvider = selectUserPresenterProvider;

        addButton = userListPresenter.addButton(GlyphIcons.ADD);
        removeButton = userListPresenter.addButton(GlyphIcons.REMOVE);

        getView().setUserView(userListPresenter.getView());
        getView().setPermissionsView(permissionsListPresenter.getView());
    }

    @Override
    protected void onBind() {
        registerHandler(userListPresenter.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
            @Override
            public void onSelectionChange(final SelectionChangeEvent event) {
                enableButtons();
                setCurrentUser(userListPresenter.getSelectedItem());
            }
        }));

        registerHandler(addButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(final ClickEvent event) {
                if (event.getNativeButton() == NativeEvent.BUTTON_LEFT) {
                    add();
                }
            }
        }));
        registerHandler(removeButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(final ClickEvent event) {
                if (event.getNativeButton() == NativeEvent.BUTTON_LEFT) {
                    remove();
                }
            }
        }));

        super.onBind();
    }

    private void enableButtons() {
        removeButton.setEnabled(userListPresenter.getSelectedItem() != null);
    }

    protected void setCurrentUser(final UserRef user) {
        permissionsListPresenter.setCurrentUser(user);
    }

    private void add() {
        final FindUserCriteria findUserCriteria = new FindUserCriteria();
        findUserCriteria.setGroup(group);

//                // If we are a group then get users and vice versa.
//                findUserCriteria.setGroup(!relatedUser.isGroup());

//        final String type = "User";
        final AdvancedUserListPresenter selectUserPresenter = selectUserPresenterProvider.get();
        selectUserPresenter.setup(findUserCriteria);

        final PopupSize popupSize = new PopupSize(400, 400, 400, 400, true);
        final PopupUiHandlers popupUiHandlers = new PopupUiHandlers() {
            @Override
            public void onHideRequest(boolean autoClose, boolean ok) {
                HidePopupEvent.fire(DocumentPermissionsTabPresenter.this, selectUserPresenter, autoClose, ok);
            }

            @Override
            public void onHide(boolean autoClose, boolean ok) {
                if (ok) {
                    final UserRef selected = selectUserPresenter.getSelectedItem();
                    if (selected != null) {
                        if (documentPermissions.getUserPermissions().get(selected) == null) {
                            documentPermissions.getUserPermissions().put(selected, new HashSet<String>());
                            userListPresenter.setSelectedItem(selected, true);
                            refreshUserList();
                        }
                    }
                }
            }
        };

        String type = "User";
        if (group) {
            type = "Group";
        }
        ShowPopupEvent.fire(DocumentPermissionsTabPresenter.this, selectUserPresenter, PopupView.PopupType.OK_CANCEL_DIALOG, popupSize, "Choose " + type + " To Add", popupUiHandlers);
    }

    private void remove() {
        final UserRef userRef = userListPresenter.getSelectedItem();
        if (userRef != null) {
            final Set<String> permissionsToRemove = documentPermissions.getUserPermissions().get(userRef);
            if (permissionsToRemove != null) {
                for (final String permission : permissionsToRemove) {
                    permissionsListPresenter.removePermission(userRef, permission);
                }
            }

            refreshUserList();
        }
    }

    public void setDocumentPermissions(final DocumentPermissions documentPermissions, final boolean group, final ChangeSet<UserPermission> changeSet) {
        this.documentPermissions = documentPermissions;
        this.group = group;
        this.changeSet = changeSet;

        if (group) {
            getView().setUsersLabelText("Groups:");
        } else {
            getView().setUsersLabelText("Users:");
        }

        final List<String> permissions = new ArrayList<String>();
        for (final String permission : documentPermissions.getAllPermissions()) {
            if (!permission.startsWith("Create")) {
                permissions.add(permission);
            }
        }

        userListPresenter.setDocumentPermissions(documentPermissions, group);
        permissionsListPresenter.setDocumentPermissions(documentPermissions, permissions, changeSet);
        refreshUserList();
    }

    private void refreshUserList() {
        userListPresenter.refresh();
    }

    public interface DocumentPermissionsTabView extends View {
        void setUserView(View view);

        void setPermissionsView(View view);

        void setUsersLabelText(String text);
    }
}
