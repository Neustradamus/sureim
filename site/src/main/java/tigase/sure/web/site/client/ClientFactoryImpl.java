/*
 * ClientFactoryImpl.java
 *
 * Tigase XMPP Web Client
 * Copyright (C) 2012-2017 "Tigase, Inc." <office@tigase.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. Look for COPYING file in the top folder.
 * If not, see http://www.gnu.org/licenses/.
 */
package tigase.sure.web.site.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.PlaceController;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;
import tigase.jaxmpp.core.client.JID;
import tigase.jaxmpp.core.client.JaxmppCore;
import tigase.jaxmpp.core.client.SessionObject;
import tigase.jaxmpp.core.client.XMPPException.ErrorCondition;
import tigase.jaxmpp.core.client.exceptions.JaxmppException;
import tigase.jaxmpp.core.client.xml.XMLException;
import tigase.jaxmpp.core.client.xmpp.modules.BookmarksModule;
import tigase.jaxmpp.core.client.xmpp.modules.ResourceBinderModule;
import tigase.jaxmpp.core.client.xmpp.modules.adhoc.AdHocCommansModule;
import tigase.jaxmpp.core.client.xmpp.modules.auth.AuthModule;
import tigase.jaxmpp.core.client.xmpp.modules.auth.SaslModule;
import tigase.jaxmpp.core.client.xmpp.modules.chat.MessageModule;
import tigase.jaxmpp.core.client.xmpp.modules.disco.DiscoveryModule;
import tigase.jaxmpp.core.client.xmpp.modules.disco.DiscoveryModule.DiscoInfoAsyncCallback;
import tigase.jaxmpp.core.client.xmpp.modules.disco.DiscoveryModule.Identity;
import tigase.jaxmpp.core.client.xmpp.modules.httpfileupload.HttpFileUploadModule;
import tigase.jaxmpp.core.client.xmpp.modules.muc.MucModule;
import tigase.jaxmpp.core.client.xmpp.modules.pubsub.PubSubModule;
import tigase.jaxmpp.core.client.xmpp.modules.streammng.StreamManagementModule;
import tigase.jaxmpp.core.client.xmpp.modules.vcard.VCardModule;
import tigase.jaxmpp.core.client.xmpp.modules.xep0136.MessageArchivingModule;
import tigase.jaxmpp.core.client.xmpp.stanzas.Stanza;
import tigase.jaxmpp.gwt.client.Presence;
import tigase.jaxmpp.gwt.client.Roster;
import tigase.sure.web.base.client.AbstractAvatarFactory;
import tigase.sure.web.base.client.auth.AuthEvent;
import tigase.sure.web.base.client.auth.AuthFailureEvent;
import tigase.sure.web.site.client.archive.ArchiveView;
import tigase.sure.web.site.client.archive.ArchiveViewImpl;
import tigase.sure.web.site.client.auth.AuthView;
import tigase.sure.web.site.client.bookmarks.BookmarksManager;
import tigase.sure.web.site.client.chat.ChatView;
import tigase.sure.web.site.client.chat.ChatViewImpl;
import tigase.sure.web.site.client.disco.DiscoView;
import tigase.sure.web.site.client.disco.DiscoViewImpl;
import tigase.sure.web.site.client.events.ServerFeaturesChangedEvent;
import tigase.sure.web.site.client.management.ManagementView;
import tigase.sure.web.site.client.management.ManagementViewImpl;
import tigase.sure.web.site.client.pubsub.PubSubPublishView;
import tigase.sure.web.site.client.pubsub.PubSubPublishViewImpl;
import tigase.sure.web.site.client.settings.SettingsView;
import tigase.sure.web.site.client.settings.SettingsViewImpl;
import tigase.sure.web.site.client.stats.StatsView;
import tigase.sure.web.site.client.stats.StatsViewImpl;

/**
 *
 * @author andrzej
 */
public class ClientFactoryImpl extends tigase.sure.web.base.client.ClientFactoryImpl implements ClientFactory {

        private static final Logger log = Logger.getLogger("ClientFactoryImpl");
        private final ActionBarFactory actionBarFactory;
        private final ArchiveView archiveView;
        private final AbstractAvatarFactory avatarFactory;
        private final AuthView authView;
        private final BookmarksManager bookmarksManager;
        private final ChatView chatView;
        private final DiscoView discoView;
        private final PubSubPublishView pubSubPublishView;
        private final SettingsView settingsView;
		private final StatsView statsView;
        private final I18n i18n = GWT.create(I18n.class);
        private final PlaceController placeController;
        private final ResourceBindHandler jaxmppBindListener = new ResourceBindHandler();
		private final ManagementViewImpl managementView;
        
        public ClientFactoryImpl() {
                super();
				
                jaxmpp().getModulesManager().register(new MessageArchivingModule());
                jaxmpp().getModulesManager().register(new BookmarksModule());
				try {
					Presence.initialize(jaxmpp());
					Roster.initialize(jaxmpp());
				} catch (JaxmppException ex) {
					log.log(Level.SEVERE, "could not initialize properly Jaxmpp instance", ex);
				}
				jaxmpp().getModulesManager().register(new MessageModule());
				jaxmpp().getModulesManager().register(new MucModule());
				jaxmpp().getModulesManager().register(new AdHocCommansModule());
				jaxmpp().getModulesManager().register(new VCardModule());
				jaxmpp().getModulesManager().register(new PubSubModule());
				jaxmpp().getModulesManager().register(new HttpFileUploadModule());
				
                placeController = new PlaceController(eventBus());
                avatarFactory = new AvatarFactory(this);
                actionBarFactory = new ActionBarFactory(this);
                
                bookmarksManager = new BookmarksManager(this);
                
                authView = new AuthView(this);
                chatView = new ChatViewImpl(this);
                archiveView = new ArchiveViewImpl(this);                
                discoView = new DiscoViewImpl(this);
				managementView = new ManagementViewImpl(this);
                pubSubPublishView = new PubSubPublishViewImpl(this);
                settingsView = new SettingsViewImpl(this);
				statsView = new StatsViewImpl(this);
                                
				jaxmpp().getEventBus().addHandler(ResourceBinderModule.ResourceBindErrorHandler.ResourceBindErrorEvent.class, jaxmppBindListener);
				jaxmpp().getEventBus().addHandler(ResourceBinderModule.ResourceBindSuccessHandler.ResourceBindSuccessEvent.class, jaxmppBindListener);
                
                jaxmpp().getEventBus().addHandler(JaxmppCore.LoggedOutHandler.LoggedOutEvent.class, new JaxmppCore.LoggedOutHandler() {
					@Override
					public void onLoggedOut(SessionObject sessionObject) {
						if (StreamManagementModule.isResumptionEnabled(sessionObject())) {
							Logger.getLogger(ClientFactoryImpl.class.getName()).severe("trying to resume broken connection");
							try {
								jaxmpp().login();
							} catch (JaxmppException ex) {
								Logger.getLogger(ClientFactoryImpl.class.getName()).log(Level.SEVERE, null, ex);
							}
						}
						else {
							eventBus().fireEvent(new AuthEvent(null));
						}
					}
				});

				jaxmpp().getEventBus().addHandler(AuthModule.AuthFailedHandler.AuthFailedEvent.class, new AuthModule.AuthFailedHandler() {

					@Override
					public void onAuthFailed(SessionObject sessionObject, SaslModule.SaslError error) throws JaxmppException {
						eventBus().fireEvent(new AuthFailureEvent(error));
					}
				});
        }

        @Override
        public ActionBarFactory actionBarFactory() {
                return actionBarFactory;
        }
        
        @Override
        public ArchiveView archiveView() {
                return archiveView;
        }
        
        @Override
        public AbstractAvatarFactory avatarFactory() {
                return avatarFactory;
        }

        @Override
        public AuthView authView() {
                return authView;
        }
        
        @Override
        public BookmarksManager bookmarksManager() {
                return bookmarksManager;
        }
        
        @Override
        public ChatView chatView() {
                return chatView;
        }

        @Override
        public DiscoView discoView() {
                return discoView;
        }
        
        public I18n i18n() {
                return i18n;
        }
        
		@Override
		public ManagementView managementView() {
			return managementView;
		}
		
        @Override
        public PlaceController placeController() {
                return placeController;
        }
        
        @Override
        public PubSubPublishView pubSubPublishView() {
                return pubSubPublishView;
        }
        
        @Override
        public SettingsView settingsView() {
                return settingsView;
        }

		@Override
		public StatsView statsView() {
				return statsView;
		}
 
		private class ResourceBindHandler implements ResourceBinderModule.ResourceBindErrorHandler, 
				ResourceBinderModule.ResourceBindSuccessHandler {

			@Override
			public void onResourceBindError(SessionObject sessionObject, ErrorCondition errorCondition) {
				MessageDialog dlg = new MessageDialog(ClientFactoryImpl.this, baseI18n().error(), errorCondition.name());
				dlg.show();
				dlg.center();
				eventBus().fireEvent(new AuthEvent(null));
			}

			@Override
			public void onResourceBindSuccess(SessionObject sessionObject, JID bindedJid) throws JaxmppException {
				try {
					eventBus().fireEvent(new ServerFeaturesChangedEvent(new ArrayList<Identity>(), new ArrayList<String>()));
					bookmarksManager().retrieve();
					jaxmpp().getModulesManager().getModule(DiscoveryModule.class).getInfo(
							JID.jidInstance(bindedJid.getDomain()), new DiscoInfoAsyncCallback(null) {

								@Override
								protected void onInfoReceived(String node, Collection<Identity> identities, Collection<String> features) throws XMLException {
									eventBus().fireEvent(new ServerFeaturesChangedEvent(identities, features));
								}

								public void onError(Stanza responseStanza, ErrorCondition error) throws JaxmppException {
									throw new UnsupportedOperationException("Not supported yet.");
								}

								public void onTimeout() throws JaxmppException {
									throw new UnsupportedOperationException("Not supported yet.");
								}

							});

//                                        if (be.getError() != null) {
//                                                Cookies.setCookie("username", 
//                                                        jaxmpp().getProperties().getUserProperty(SessionObject.USER_BARE_JID).toString(),
//                                                        new Date(new Date().getTime() + 24*60*60*1000*7));
//                                                Cookies.setCookie("password", 
//                                                        jaxmpp().getProperties().getUserProperty(SessionObject.PASSWORD).toString(),
//                                                        new Date(new Date().getTime() + 24*60*60*1000*7));
//                                        }
					eventBus().fireEvent(new AuthEvent(bindedJid));
				} catch (Exception ex) {
					log.log(Level.WARNING, "exception firing auth event", ex);
				}
			}
			
		}
}
