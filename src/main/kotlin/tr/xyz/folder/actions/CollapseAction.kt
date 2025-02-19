// src/main/kotlin/tr/xyz/folder/actions/CollapseAction.kt
package tr.xyz.folder.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import tr.xyz.folder.folding.KotlinFoldingStrategy

class CollapseAction : AnAction() {
	
	private val foldingStrategy = KotlinFoldingStrategy()
	
	override fun actionPerformed(e: AnActionEvent) {
		val editor = e.getData(CommonDataKeys.EDITOR) ?: return
		val psiFile = e.getData(CommonDataKeys.PSI_FILE) ?: return
		
		foldingStrategy.collapse(editor, psiFile)
	}
	
	override fun update(e: AnActionEvent) {
		val editor = e.getData(CommonDataKeys.EDITOR)
		val psiFile = e.getData(CommonDataKeys.PSI_FILE)
		e.presentation.isEnabled = editor != null && psiFile != null && psiFile.language.displayName == "Kotlin" // Enable only for Kotlin files
		
	}
}