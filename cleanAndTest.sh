#!/bin/bash

#
#  Copyright 2017 Fernando Raul Bucci
#
#  Licensed under the Apache License, Version 2.0 (the "License");
#  you may not use this file except in compliance with the License.
#  You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
#  Unless required by applicable law or agreed to in writing, software
#  distributed under the License is distributed on an "AS IS" BASIS,
#  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
#  See the License for the specific language governing permissions and
#  limitations under the License.
#

set -e

echo "Deleting Local M2 repository"
rm -rf ~/.m2/repository

echo "Building plugin"
cd hivemigration-gradle-plugin
rm -rf .gradle

gradle install

echo "Testing plugin"
gradle test

echo "Running sample"
cd ../hivemigration-gradle-sample
rm -rf .gradle

gradle migrate

echo "Done"
