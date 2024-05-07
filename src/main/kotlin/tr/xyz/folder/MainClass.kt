package tr.xyz.folder

import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileEditor.FileEditorManagerListener
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectManagerListener
import com.intellij.openapi.startup.StartupActivity
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.util.PsiUtilCore
import com.intellij.psi.util.elementType

class MainClass : ProjectManagerListener, StartupActivity, AnAction() {

    private lateinit var project: Project

    private fun onFileOpen(source: FileEditorManager, file: VirtualFile) {
        val editors = EditorFactory.getInstance().allEditors
        val psi = PsiUtilCore.getPsiFile(source.project, file)

        if (psi.language.displayName == "Kotlin")
            editors.forEach { collapse(it, psi) }
    }

    private fun onProjectOpen(project: Project) {
        this.project = project
        project.messageBus.connect()
            .subscribe(FileEditorManagerListener.FILE_EDITOR_MANAGER, object : FileEditorManagerListener {
                override fun fileOpened(source: FileEditorManager, file: VirtualFile) {
                    super.fileOpened(source, file)
                    onFileOpen(source, file)
                }
            })

        val editors = EditorFactory.getInstance().allEditors
        val manager = PsiDocumentManager.getInstance(project)
        editors.forEach { editor ->
            val psi = manager.getPsiFile(editor.document)
            if (psi?.language?.displayName == "Kotlin")
                collapse(editor, psi)
        }
    }

    override fun runActivity(project: Project) {
        onProjectOpen(project)
    }

    override fun actionPerformed(e: AnActionEvent) {
        // runActivity(e.getData(CommonDataKeys.PROJECT)!!)
        val editor = e.getData(CommonDataKeys.EDITOR) ?: return
        val psi = e.getData(CommonDataKeys.PSI_FILE) ?: return

        if (psi.language.displayName != "Kotlin") return

        val caret = editor.caretModel.primaryCaret
        val element: PsiElement = psi.findElementAt(caret.offset) ?: return
        val lang = psi.language.displayName
        val elementType = element.node.elementType.toString()

        println("================================================================")
        println("lang                           : $lang")
        println("element Type                   : $elementType")
        println("caret.offset                   : ${caret.offset}")
        println("element.textRange.startOffset  : ${element.textRange.startOffset}")
        println("element.textRange.endOffset    : ${element.textRange.endOffset}")

        collapse(editor, psi)
    }

    override fun update(e: AnActionEvent) {
        super.update(e)
    }

    private val REGIONS_TO_COLLAPSE =
        listOf(/*"CLASS_BODY",*/"FUN",
            "PROPERTY_ACCESSOR",
            "OBJECT_DECLARATION",
            "CLASS_INITIALIZER",
            "VALUE_ARGUMENT_LIST",
            "PROPERTY",
            "REFERENCE_EXPRESSION"
        )
    private val TYPES = listOf("CLASS", "object")

    private fun collapse(editor: Editor, psi: PsiFile) {

        /*psi.children.forEach {
            println("children : ${it.elementType}")
        }*/
        val classes = psi.children.filter {
            it.elementType.toString() == "REFERENCE_EXPRESSION" || it.elementType.toString() == "PROPERTY" || it.elementType.toString() == "CLASS" || it.elementType.toString() == "OBJECT_DECLARATION" || it.elementType.toString() == "FUN"
        }
        classes.forEach { classElement ->
            val bodies = classElement.children[0]
            val funs = bodies.children.filter { it.elementType.toString() == "FUN" }
            println("funs : ${funs.size}")
            println("classElement : ${classElement.elementType}")

            ApplicationManager.getApplication().runReadAction {
                editor.foldingModel.runBatchFoldingOperation {
                    editor.foldingModel.allFoldRegions.forEach { region ->
                        // Do not collapse if caret is in the region
                        if (editor.caretModel.primaryCaret.offset !in region.textRange) {
                            val el = psi.findElementAt(region.startOffset)

                            if (el != null) {
                                val elementString = el.elementType.toString()
                                if (elementString == "LBRACE" || elementString == "LPAR") {
                                    if (el.parent.elementType.toString() in REGIONS_TO_COLLAPSE && region.isExpanded) {
                                        region.isExpanded = false
                                    }
                                    if (el.parent.parent.elementType.toString() in REGIONS_TO_COLLAPSE && region.isExpanded) {
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

    override fun getActionUpdateThread(): ActionUpdateThread = ActionUpdateThread.BGT
}