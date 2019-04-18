# Asynchronous
Wrapper para "AsyncTask" que conhece o ciclo do host. (Activity/Fragment).

## [ ![Download](https://api.bintray.com/packages/diogo0liveira/android/asynchronous/images/download.svg?version=1.0.0) ](https://bintray.com/diogo0liveira/android/asynchronous/1.0.0/link)

```groovy
dependencies {
    implementation 'com.dao.asynchronous:asynchronous:1.0.0'
}
```

```kotlin
class ExecuteTask : SingleAsynchronous<String, Boolean>()
{
    override fun doInBackground(param: String): Boolean
    {
        return (param.contains("test"))
    }
}
```

```kotlin
val executeTask = AsynchronousProviders.of(ExecuteTask::class.java, supportFragmentManager)
executeTask.attachListener(object: OnAsynchronousListener<Boolean>() {
    override fun onBegin(tag: String)
    {
        showLoading()
    }
    override fun onSuccess(tag: String, result: Boolean)
    {
        if(result)
        {
            Toast.makeText(context, "executed successfully", Toast.LENGTH_LONG).show()
        }
    }
    override fun onError(tag: String, e: Exception)
    {
        Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
    }
    override fun onFinish(task: Asynchronous?)
    {
        hideLoading()
    }
})
```
