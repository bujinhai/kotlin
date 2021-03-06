/*
 * Copyright 2010-2017 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jetbrains.kotlin.android.parcel

import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.ClassKind
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.resolve.extensions.SyntheticResolveExtension

class ParcelBoxTestWithSerializableLikeExtension : AbstractParcelBoxTest() {
    fun testSimple() = doTest("plugins/android-extensions/android-extensions-compiler/testData/parcel/box/simple.kt")

    override fun setupEnvironment(environment: KotlinCoreEnvironment) {
        super.setupEnvironment(environment)
        SyntheticResolveExtension.registerExtension(environment.project, SerializableLike())
    }

    private class SerializableLike : SyntheticResolveExtension {
        override fun getSyntheticCompanionObjectNameIfNeeded(thisDescriptor: ClassDescriptor): Name? {
            fun ClassDescriptor.isSerializableLike() = annotations.hasAnnotation(FqName("test.SerializableLike"))

            return when {
                thisDescriptor.kind == ClassKind.CLASS && thisDescriptor.isSerializableLike() -> Name.identifier("Companion")
                else -> return null
            }
        }
    }
}
