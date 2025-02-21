// src/main/kotlin/tr/xyz/folder/folding/KotlinFoldingStrategy.kt
package tr.xyz.folder.folding

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.editor.Editor
import com.intellij.psi.PsiFile
import com.intellij.psi.util.elementType
import tr.xyz.folder.config.PluginSettings


class KotlinFoldingStrategy {
	
	fun collapse(editor: Editor, psi: PsiFile) {
		if (!psi.language.displayName.equals("Kotlin", ignoreCase = true)) {
			return
		}
		val settings = PluginSettings.getInstance()
		if (!settings.enabled) return
		
		val t = run {
			6
		}
		val regionsToCollapse = settings.regionsToCollapse
		
		ApplicationManager.getApplication().runReadAction {
			editor.foldingModel.runBatchFoldingOperation {
				editor.foldingModel.allFoldRegions.forEach { region ->
					if (editor.caretModel.primaryCaret.offset !in region.textRange) {
						val el = psi.findElementAt(region.startOffset)
						if (el != null) {
							val elementString = el.elementType.toString()
							if (elementString == "LBRACE" || elementString == "LPAR") {
								if ((el.parent.elementType.toString() in regionsToCollapse && region.isExpanded) ||
								    (el.parent.parent.elementType.toString() in regionsToCollapse && region.isExpanded)
								) {
									region.isExpanded = false
								}
							}
						}
					}
				}
			}
		}
	}
}