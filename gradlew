#!/bin/sh
exec gradle $@ 2>/dev/null || exec ./gradle/wrapper/gradle-wrapper.jar $@