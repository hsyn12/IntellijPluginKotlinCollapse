// src/main/kotlin/tr/xyz/folder/StartupActivity.kt

package tr.xyz.folder

import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.ProjectActivity
import com.intellij.psi.PsiDocumentManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import tr.xyz.folder.dev.readAction
import tr.xyz.folder.folding.KotlinFoldingStrategy

class StartupActivity : ProjectActivity, DumbAware { // StartupActivity yerine ProjectActivity
	private val foldingStrategy = KotlinFoldingStrategy()
	
	override suspend fun execute(project: Project) {
		CoroutineScope(Dispatchers.Main).launch { // Coroutine içinde
			try {
				val fileEditorManager = FileEditorManager.getInstance(project)
				EditorFactory.getInstance().allEditors.forEach { editor ->
					val psiFile = PsiDocumentManager.getInstance(project).getPsiFile(editor.document) ?: return@forEach
					if (psiFile.language.displayName == "Kotlin") {
						readAction {
							foldingStrategy.collapse(editor, psiFile)
						}
					}
				}
			}
			catch (_: Exception) {
			}
		}
	}
}