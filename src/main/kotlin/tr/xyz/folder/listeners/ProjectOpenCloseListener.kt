// src/main/kotlin/tr/xyz/folder/listeners/ProjectOpenCloseListener.kt
package tr.xyz.folder.listeners

import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileEditor.FileEditorManagerListener
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.util.PsiUtilCore
import tr.xyz.folder.dev.readAction
import tr.xyz.folder.folding.KotlinFoldingStrategy

class ProjectOpenCloseListener : com.intellij.openapi.startup.ProjectActivity, DumbAware { // ProjectManagerListener yerine ProjectActivity
	private val foldingStrategy = KotlinFoldingStrategy()
	
	override suspend fun execute(project: Project) { // projectOpened yerine execute
		val fileEditorManager = FileEditorManager.getInstance(project)
		
		// listen for file opened
		project.messageBus.connect().subscribe(
			FileEditorManagerListener.FILE_EDITOR_MANAGER,
			object : FileEditorManagerListener {
				override fun fileOpened(source: FileEditorManager, file: VirtualFile) {
					val psiFile = PsiUtilCore.getPsiFile(project, file)
					EditorFactory.getInstance().allEditors.forEach { editor ->
						if (psiFile.language.displayName == "Kotlin") {
							readAction {
								foldingStrategy.collapse(editor, psiFile)
							}
						}
					}
				}
			})
	}
}