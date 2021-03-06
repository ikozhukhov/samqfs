# Generated automatically from Makefile.in by configure.
# Makefile.in generated automatically by automake 1.1l from Makefile.am

# Copyright (C) 1994, 1995, 1996 Free Software Foundation, Inc.
# This Makefile.in is free software; the Free Software Foundation
# gives unlimited permission to copy, distribute and modify it.

SHELL = /bin/sh

DEPTH = ../../../..
include $(DEPTH)/mk/common.mk

srcdir = .
top_srcdir = ..
prefix = /usr/local
exec_prefix = ${prefix}

bindir = ${exec_prefix}/bin
sbindir = ${exec_prefix}/sbin
libexecdir = ${exec_prefix}/libexec
datadir = ${prefix}/share
sysconfdir = ${prefix}/etc
sharedstatedir = ${prefix}/com
localstatedir = ${prefix}/var
libdir = ${exec_prefix}/lib
infodir = ${prefix}/info
mandir = ${prefix}/man
includedir = ${prefix}/include
oldincludedir = /usr/include

pkgdatadir = $(datadir)/fileutils
pkglibdir = $(libdir)/fileutils
pkgincludedir = $(includedir)/fileutils

top_builddir = ..

INSTALL = .././install-sh -c
INSTALL_PROGRAM = ${INSTALL}
INSTALL_DATA = ${INSTALL} -m 644
INSTALL_SCRIPT = ${INSTALL_PROGRAM}
transform = s,x,x,
YACC = bison -y
GENCAT = 
PERL = /usr/local/bin/perl
MV = /bin/mv
GMSGFMT = /bin/msgfmt
CC = ${GCC}
RANLIB = ranlib
LN = /bin/ln
RM = /bin/rm

info_TEXINFOS = fileutils.texi

# FIXME: remove texinfo.tex when automake has been fixed to include it
# automatically
EXTRA_DIST = perm.texi getdate.texi texinfo.tex

# Tell makeinfo to put everything in a single info file: sh-utils.info.
# Otherwise, it would also generate files with names like sh-utils.info-[123],
# and those names all map to one 14-byte name (sh-utils.info-) on some crufty
# old systems.
MAKEINFO = makeinfo --no-split
mkinstalldirs = $(SHELL) $(top_srcdir)/mkinstalldirs
CONFIG_HEADER = ../config.h
CONFIG_CLEAN_FILES = 
TEXI2DVI = texi2dvi
INFO_DEPS = fileutils.info
DVIS = fileutils.dvi
TEXINFOS = fileutils.texi
DIST_COMMON =  Makefile.am Makefile.in mdate-sh stamp-vti texinfo.tex \
version.texi

PACKAGE = fileutils
VERSION = 3.16

DISTFILES = $(DIST_COMMON) $(SOURCES) $(HEADERS) \
	$(TEXINFOS) $(MANS) $(EXTRA_DIST)

TAR = tar
default: all

.SUFFIXES:
.SUFFIXES: .texi .texinfo .info .dvi .ps
$(srcdir)/Makefile.in: #M# Makefile.am $(top_srcdir)/configure.in $(ACLOCAL) 
	cd $(top_srcdir) && automake --gnits doc/Makefile

Makefile: #SUN# $(srcdir)/Makefile.in $(top_builddir)/config.status $(BUILT_SOURCES)
	cd $(top_builddir) \
	  && CONFIG_FILES=$(subdir)/$@ CONFIG_HEADERS= $(SHELL) ./config.status

version.texi: #M#stamp-vti

stamp-vti: fileutils.texi $(top_srcdir)/configure.in
	echo "@set UPDATED `cd $(srcdir) \
	  && $(SHELL) ./mdate-sh fileutils.texi`" > vti.tmp
	echo "@set EDITION $(VERSION)" >> vti.tmp
	echo "@set VERSION $(VERSION)" >> vti.tmp
	cmp -s vti.tmp $(srcdir)/version.texi || cp vti.tmp $(srcdir)/version.texi
	rm vti.tmp
	echo timestamp > $(srcdir)/stamp-vti

mostlyclean-vti:
	rm -f vti.tmp

clean-vti:

distclean-vti:

maintainer-clean-vti:
	#M#rm -f stamp-vti version.texi

fileutils.info: fileutils.texi version.texi
fileutils.dvi: fileutils.texi version.texi

DVIPS = dvips

.texi.info:
	cd $(srcdir) \
	  && $(MAKEINFO) `echo $< | sed 's,.*/,,'`

.texi.dvi:
	TEXINPUTS=$(srcdir):$$TEXINPUTS \
	  MAKEINFO='$(MAKEINFO) -I $(srcdir)' $(TEXI2DVI) $<

.texinfo.info:
	cd $(srcdir) \
	  && $(MAKEINFO) `echo $< | sed 's,.*/,,'`

.texinfo.dvi:
	TEXINPUTS=$(srcdir):$$TEXINPUTS $(TEXI2DVI) $<
.dvi.ps:
	$(DVIPS) $< -o $@

install-info-am: $(INFO_DEPS)
	$(NORMAL_INSTALL)
	$(mkinstalldirs) $(infodir)
	@for file in $(INFO_DEPS); do \
	  for ifile in `cd $(srcdir) && echo $$file $$file-[0-9] $$file-[0-9][0-9]`; do \
	    if test -f $(srcdir)/$$ifile; then \
	      echo "$(INSTALL_DATA) $(srcdir)/$$ifile $(infodir)/$$ifile"; \
	      $(INSTALL_DATA) $(srcdir)/$$ifile $(infodir)/$$ifile; \
	    else : ; fi; \
	  done; \
	done
	$(POST_INSTALL)
	@if $(SHELL) -c 'install-info --version' >/dev/null 2>&1; then \
	  for file in $(INFO_DEPS); do \
	    echo "install-info --info-dir=$(infodir) $(infodir)/$$file";\
	    install-info --info-dir=$(infodir) $(infodir)/$$file; :;\
	  done; \
	else : ; fi

uninstall-info:
	if $(SHELL) -c 'install-info --version' >/dev/null 2>&1; then \
	  ii=yes; \
	else ii=; fi; \
	for file in $(INFO_DEPS); do \
	  (cd $(infodir) && rm -f $$file $$file-[0-9] $$file-[0-9][0-9]); \
	  test -z $ii || install-info --info-dir=$(infodir) --remove $$file; \
	done

dist-info: $(INFO_DEPS)
	for base in $(INFO_DEPS); do \
	  d=$(srcdir); \
	  for file in `cd $$d && eval echo $$base*`; do \
	    test -f $(distdir)/$$file \
	    || ln $$d/$$file $(distdir)/$$file 2> /dev/null \
	    || cp -p $$d/$$file $(distdir)/$$file; \
	  done; \
	done

mostlyclean-info:
	rm -f fileutils.aux fileutils.cp fileutils.cps fileutils.dvi \
	  fileutils.fn fileutils.fns fileutils.ky fileutils.log \
	  fileutils.pg fileutils.toc fileutils.tp fileutils.vr \
	  fileutils.op

clean-info:

distclean-info:

maintainer-clean-info:
	for i in $(INFO_DEPS); do rm -f `eval echo $$i*`; done
tags: TAGS
TAGS:

distdir = $(top_builddir)/$(PACKAGE)-$(VERSION)/$(subdir)

subdir = doc
distdir: $(DISTFILES)
	@for file in $(DISTFILES); do \
	  d=$(srcdir); \
	  test -f $(distdir)/$$file \
	  || ln $$d/$$file $(distdir)/$$file 2> /dev/null \
	  || cp -p $$d/$$file $(distdir)/$$file; \
	done
	$(MAKE) distdir="$(distdir)" dist-info
info: $(INFO_DEPS)
dvi: $(DVIS)
check: all
	$(MAKE)
installcheck:
install-exec: 
	$(NORMAL_INSTALL)

install-data: install-info-am
	$(NORMAL_INSTALL)

install: install-exec install-data all
	@:

uninstall: uninstall-info

all: $(INFO_DEPS) Makefile

install-strip:
	$(MAKE) INSTALL_PROGRAM='$(INSTALL_PROGRAM) -s' install
installdirs:
	$(mkinstalldirs)  $(infodir)

mostlyclean-generic:
	test -z "$(MOSTLYCLEANFILES)" || rm -f $(MOSTLYCLEANFILES)

clean-generic:
	test -z "$(CLEANFILES)" || rm -f $(CLEANFILES)

distclean-generic:
	rm -f Makefile $(DISTCLEANFILES)
	rm -f config.cache config.log stamp-h
	test -z "$(CONFIG_CLEAN_FILES)" || rm -f $(CONFIG_CLEAN_FILES)

maintainer-clean-generic:
	test -z "$(MAINTAINERCLEANFILES)" || rm -f $(MAINTAINERCLEANFILES)
	test -z "$(BUILT_SOURCES)" || rm -f $(BUILT_SOURCES)
mostlyclean:  mostlyclean-vti mostlyclean-info mostlyclean-generic

clean:  clean-vti clean-info clean-generic mostlyclean

distclean:  distclean-vti distclean-info distclean-generic clean
	rm -f config.status

maintainer-clean:  maintainer-clean-vti maintainer-clean-info \
		maintainer-clean-generic distclean
	@echo "This command is intended for maintainers to use;"
	@echo "it deletes files that may require special tools to rebuild."

.PHONY: default mostlyclean-vti distclean-vti clean-vti \
maintainer-clean-vti install-info-am uninstall-info mostlyclean-info \
distclean-info clean-info maintainer-clean-info tags distdir info dvi \
installcheck install-exec install-data install uninstall all \
installdirs mostlyclean-generic distclean-generic clean-generic \
maintainer-clean-generic clean mostlyclean distclean maintainer-clean

# SUN targets:
DIST = $(DIST_COMMON) $(TEXINFOS) $(EXTRA_DIST)

include $(DEPTH)/mk/targets.mk

# Tell versions [3.59,3.63) of GNU make to not export all variables.
# Otherwise a system limit (for SysV at least) may be exceeded.
.NOEXPORT:

include $(DEPTH)/mk/depend.mk
