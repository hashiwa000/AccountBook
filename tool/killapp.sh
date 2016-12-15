#!/bin/bash
ps -ef | grep -v grep | grep jp.hashiwa.accountbook.Application | awk '{print $2}' | xargs kill

