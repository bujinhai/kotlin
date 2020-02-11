package org.jetbrains.kotlin.tools.projectWizard.wizard.ui.setting

import com.intellij.ui.components.panels.VerticalLayout
import org.jetbrains.kotlin.tools.projectWizard.core.ValuesReadingContext
import org.jetbrains.kotlin.tools.projectWizard.core.entity.SettingReference
import org.jetbrains.kotlin.tools.projectWizard.core.entity.ValidationResult
import org.jetbrains.kotlin.tools.projectWizard.core.entity.isSpecificError
import org.jetbrains.kotlin.tools.projectWizard.wizard.ui.DynamicComponent
import org.jetbrains.kotlin.tools.projectWizard.wizard.ui.FocusableComponent
import org.jetbrains.kotlin.tools.projectWizard.wizard.ui.PanelWithStatusText
import javax.swing.JComponent

interface ErrorAwareComponent {
    fun findComponentWithError(error: ValidationResult.ValidationError): FocusableComponent?
}

class SettingsList(
    settings: List<SettingReference<*, *>>,
    private val valuesReadingContext: ValuesReadingContext
) : DynamicComponent(valuesReadingContext), ErrorAwareComponent {
    private val panel = PanelWithStatusText(VerticalLayout(5), "This module has no settings to configure")

    var settingComponents: List<SettingComponent<*, *>> = emptyList()
        private set

    init {
        setSettings(settings)
    }

    override val component: JComponent = panel

    override fun onInit() {
        super.onInit()
        settingComponents.forEach { it.onInit() }
    }

    fun setSettings(settings: List<SettingReference<*, *>>) {
        panel.isStatusTextVisible = settings.isEmpty()
        panel.removeAll()
        settingComponents = settings.map { setting ->
            DefaultSettingComponent.create(setting, valuesReadingContext)
        }
        settingComponents.forEach { setting -> setting.onInit(); panel.add(setting.component) }
    }

    override fun findComponentWithError(error: ValidationResult.ValidationError) =
        settingComponents.firstOrNull { component ->
            val value = component.value ?: return@firstOrNull false
            val result = component.setting.validator.validate(valuesReadingContext, value)
           result isSpecificError error
        }
}