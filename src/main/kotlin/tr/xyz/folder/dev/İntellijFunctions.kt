package tr.xyz.folder.dev

import com.intellij.openapi.application.ApplicationManager

inline fun readAction(crossinline action: () -> Unit) {
	ApplicationManager.getApplication().runReadAction {
		action()
	}
}