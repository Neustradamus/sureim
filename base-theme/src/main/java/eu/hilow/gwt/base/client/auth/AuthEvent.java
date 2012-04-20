/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.hilow.gwt.base.client.auth;

import com.google.web.bindery.event.shared.Event;
import tigase.jaxmpp.core.client.JID;

/**
 *
 * @author andrzej
 */
public class AuthEvent extends Event<AuthHandler> {

        public static final Type<AuthHandler> TYPE = new Type<AuthHandler>();
        
        private final JID jid;
        
        public AuthEvent(JID jid) {
                this.jid = jid;
        }
        
        @Override
        public Type<AuthHandler> getAssociatedType() {
                return TYPE;
        }

        @Override
        protected void dispatch(AuthHandler handler) {
                if (jid == null) {
                        handler.deauthenticated();
                }
                else {
                        handler.authenticated(jid);
                }
        }

        
}
