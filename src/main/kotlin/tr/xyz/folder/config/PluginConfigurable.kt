// src/main/kotlin/tr/xyz/folder/config/PluginConfigurable.kt
package tr.xyz.folder.config

import com.intellij.openapi.options.Configurable
import com.intellij.ui.components.JBCheckBox
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBScrollPane
import com.intellij.util.ui.FormBuilder
import com.intellij.util.ui.JBUI
import javax.swing.BoxLayout
import javax.swing.JComponent
import javax.swing.JPanel

class PluginConfigurable : Configurable {
	
	private lateinit var mainPanel: JPanel
	private lateinit var enabledCheckBox: JBCheckBox
	private lateinit var regionsPanel: JPanel
	private var settings = PluginSettings.getInstance()
	
	private val REGIONS = arrayOf(
		"FUN", "PROPERTY_ACCESSOR", "OBJECT_DECLARATION", "CLASS_INITIALIZER",
		"VALUE_ARGUMENT_LIST", "PROPERTY", "CLASS", "INTERFACE", "ENUM", "ANNOTATION",
		"TYPE_ALIAS", "CONSTRUCTOR", "GETTER", "SETTER", "FIELD", "FUNCTION_LITERAL"
	)
	
	private val regionCheckBoxes = mutableMapOf<String, JBCheckBox>()
	
	override fun getDisplayName(): String {
		return "Kotlin Collapse"
	}
	
	override fun createComponent(): JComponent {
		enabledCheckBox = JBCheckBox("Enable Kotlin Collapse", settings.enabled)
		
		regionsPanel = JPanel()
		regionsPanel.layout = BoxLayout(regionsPanel, BoxLayout.Y_AXIS)
		
		REGIONS.forEach { region ->
			val checkBox = JBCheckBox(region, settings.regionsToCollapse.contains(region))
			regionCheckBoxes[region] = checkBox
			regionsPanel.add(checkBox)
		}
		
		mainPanel = FormBuilder.createFormBuilder()
			.addComponent(enabledCheckBox)
			.addSeparator()
			.addLabeledComponent(JBLabel("Regions to collapse:"), JBScrollPane(regionsPanel), 10, false)
			.panel
		
		mainPanel.border = JBUI.Borders.empty(10)
		return mainPanel
	}
	
	override fun isModified(): Boolean {
		return enabledCheckBox.isSelected != settings.enabled || regionCheckBoxes.any { (region, checkBox) ->
			settings.regionsToCollapse.contains(region) != checkBox.isSelected
		}
	}
	
	override fun apply() {
		settings.enabled = enabledCheckBox.isSelected
		settings.regionsToCollapse = regionCheckBoxes.filterValues { it.isSelected }.keys.toList()
	}
	
	override fun reset() {
		enabledCheckBox.isSelected = settings.enabled
		regionCheckBoxes.forEach { (region, checkBox) ->
			checkBox.isSelected = settings.regionsToCollapse.contains(region)
		}
	}
}