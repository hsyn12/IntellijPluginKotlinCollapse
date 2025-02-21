// src/main/kotlin/tr/xyz/folder/config/PluginSettings.kt
package tr.xyz.folder.config

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.util.xmlb.XmlSerializerUtil

@State(
	name = "tr.xyz.folder.config.PluginSettings",
	storages = [Storage("KotlinCollapseSettings.xml")]
)
class PluginSettings : PersistentStateComponent<PluginSettings> {
	var regionsToCollapse: List<String> = listOf(
		"FUN", "PROPERTY_ACCESSOR", "OBJECT_DECLARATION",
		"CLASS_INITIALIZER", "VALUE_ARGUMENT_LIST", "PROPERTY", "FUNCTION_LITERAL"
	)
	var typesToCollapse: List<String> = listOf("CLASS", "object")
	var enabled: Boolean = true
	
	override fun getState(): PluginSettings {
		return this
	}
	
	override fun loadState(state: PluginSettings) {
		XmlSerializerUtil.copyBean(state, this)
	}
	
	companion object {
		fun getInstance(): PluginSettings {
			return ApplicationManager.getApplication().getService(PluginSettings::class.java)
		}
	}
}