/*
 * BookmarksEvent.java
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
package tigase.sure.web.site.client.bookmarks;

import com.google.web.bindery.event.shared.Event;
import java.util.List;
import tigase.jaxmpp.core.client.xml.Element;

/**
 *
 * @author andrzej
 */
public class BookmarksEvent extends Event<BookmarksHandler> {

        public static final Type<BookmarksHandler> TYPE = new Type<BookmarksHandler>();
        
        private final List<Element> bookmarks;
        
        public BookmarksEvent(List<Element> bookmarks) {
                this.bookmarks = bookmarks;
        }
        
        @Override
        public Type<BookmarksHandler> getAssociatedType() {
                return TYPE;
        }

        @Override
        protected void dispatch(BookmarksHandler handler) {
                handler.bookmarksChanged(bookmarks);                
        }
        
}
