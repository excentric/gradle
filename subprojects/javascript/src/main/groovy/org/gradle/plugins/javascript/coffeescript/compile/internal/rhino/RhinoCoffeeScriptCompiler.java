/*
 * Copyright 2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.gradle.plugins.javascript.coffeescript.compile.internal.rhino;

import org.gradle.api.logging.LogLevel;
import org.gradle.api.tasks.WorkResult;
import org.gradle.plugins.javascript.coffeescript.CoffeeScriptCompileSpec;
import org.gradle.plugins.javascript.coffeescript.CoffeeScriptCompiler;
import org.gradle.plugins.javascript.coffeescript.compile.internal.SerializableCoffeeScriptCompileSpec;
import org.gradle.plugins.javascript.rhino.worker.BlockingRhinoWorkerResultReceiver;
import org.gradle.plugins.javascript.rhino.worker.RhinoWorkerManager;
import org.gradle.process.internal.WorkerProcess;

import java.io.File;

public class RhinoCoffeeScriptCompiler implements CoffeeScriptCompiler {

    private final RhinoWorkerManager workerManager;
    private final Iterable<File> rhinoClasspath;
    private final LogLevel logLevel;
    private final File workingDir;

    public RhinoCoffeeScriptCompiler(RhinoWorkerManager workerManager, Iterable<File> rhinoClasspath, LogLevel logLevel, File workingDir) {
        this.workerManager = workerManager;
        this.rhinoClasspath = rhinoClasspath;
        this.logLevel = logLevel;
        this.workingDir = workingDir;
    }

    public WorkResult compile(CoffeeScriptCompileSpec spec) {
        WorkerProcess workerProcess = workerManager.createWorkerProcess(rhinoClasspath, logLevel, CoffeeScriptCompilerWorker.class, workingDir, null);
        BlockingRhinoWorkerResultReceiver<Void> blockingReceiver = new BlockingRhinoWorkerResultReceiver<Void>(Void.class);
        blockingReceiver.waitForResult(workerProcess, new SerializableCoffeeScriptCompileSpec(spec));
        return new WorkResult() {
            public boolean getDidWork() {
                return true;
            }
        };
    }

}