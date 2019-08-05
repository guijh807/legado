package io.legado.app.ui.widget.page

class TextPageFactory private constructor(dataSource: DataSource) : PageFactory<TextPage>(dataSource) {

    companion object{
        fun create(dataSource: DataSource): TextPageFactory{
            return TextPageFactory(dataSource)
        }
    }

    private var index: Int = 0

    override fun pageAt(index: Int): TextPage {
        TODO("todo...")
    }

    override fun nextPage(): TextPage {
        return TextPage(index.plus(1), "index：$index")
    }

    override fun previousPage(): TextPage {
        return TextPage(index.minus(1), "index：$index")
    }


}