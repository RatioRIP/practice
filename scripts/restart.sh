#!/bin/sh

ssh pcr@staudie 'kill -9 $(pgrep java --group pcr)'