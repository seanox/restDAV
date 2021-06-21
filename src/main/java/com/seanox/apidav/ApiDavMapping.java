/**
 * LIZENZBEDINGUNGEN - Seanox Software Solutions ist ein Open-Source-Projekt,
 * im Folgenden Seanox Software Solutions oder kurz Seanox genannt.
 * Diese Software unterliegt der Version 2 der GNU General Public License.
 *
 * apiDAV, API-WebDAV mapping for Spring Boot
 * Copyright (C) 2021 Seanox Software Solutions
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of version 2 of the GNU General Public License as published by the
 * Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package com.seanox.apidav;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.util.Date;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ApiDavMapping {

    String path();
    long contentLength()  default -1;
    String contentType()  default "application/octet-stream";
    String lastModified() default "";
    String creationDate() default "";
    boolean isReadOnly()  default false;
    boolean isHidden()    default false;
    boolean isPermitted() default true;
    boolean isAccepted()  default true;

    @Getter(AccessLevel.PACKAGE)
    class MappingAnnotation extends Annotation {

        private final long contentLength;
        private final String contentType;
        private final Date creationDate;
        private final Date lastModified;
        private final boolean isReadOnly;
        private final boolean isHidden;
        private final boolean isPermitted;
        private final boolean isAccepted;

        @Builder(access=AccessLevel.PRIVATE)
        MappingAnnotation(final String path, final Type type, final Object object, final Method method,
                        final long contentLength, final String contentType, final Date creationDate, final Date lastModified,
                        final boolean isReadOnly, final boolean isHidden, final boolean isPermitted, final boolean isAccepted) {
            super(path, type, object, method);

            this.contentLength = contentLength;
            this.contentType   = contentType;
            this.creationDate  = creationDate;
            this.lastModified  = lastModified;
            this.isReadOnly    = isReadOnly;
            this.isHidden      = isHidden;
            this.isPermitted   = isPermitted;
            this.isAccepted    = isAccepted;
        }

        static MappingAnnotation create(final ApiDavMapping apiDavMapping, final Object object, final Method method)
                throws AnnotationException {

            Date creationDate;
            try {creationDate = Annotation.convertDateTime(apiDavMapping.creationDate());
            } catch (ParseException exception) {
                throw new AnnotationException("Invalid value for creationDate: " + apiDavMapping.creationDate().trim());
            }

            Date lastModified;
            try {lastModified = Annotation.convertDateTime(apiDavMapping.lastModified());
            } catch (ParseException exception) {
                throw new AnnotationException("Invalid value for lastModified: " + apiDavMapping.lastModified().trim());
            }

            return MappingAnnotation.builder()
                    .path(apiDavMapping.path())
                    .type(Type.Mapping)
                    .object(object)
                    .method(method)

                    .contentLength(apiDavMapping.contentLength())
                    .contentType(Annotation.convertText(apiDavMapping.contentType()))
                    .creationDate(creationDate)
                    .lastModified(lastModified)
                    .isReadOnly(apiDavMapping.isReadOnly())
                    .isHidden(apiDavMapping.isHidden())
                    .isPermitted(apiDavMapping.isPermitted())
                    .isAccepted(apiDavMapping.isAccepted())
                    .build();
        }
    }
}