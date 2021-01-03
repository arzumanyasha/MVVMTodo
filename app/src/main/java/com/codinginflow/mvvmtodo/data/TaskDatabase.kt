package com.codinginflow.mvvmtodo.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.codinginflow.mvvmtodo.di.ApplicationScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Provider

@Database(entities = [Task::class], version = 0)
abstract class TaskDatabase : RoomDatabase() {

    abstract fun taskDao(): TaskDao

    //not so good practice. Just want to add some dummy data
    //@Inject tells dagger how we create this class and if we had parameters - the way how we
    // want to pass it through the constructor
    //
    //To make db operations we need dao. For creating dao we need db. For creating db we need callback.
    //For creating callback we need db. There is the problem with circular dependencies. Db needs a callback
    // and callback needs db for instantiating.
    // OnCreate method not called when we add callback, it's called after build method complete
    //
    // So we need a way to build callback a little bit later
    //by Provider we can get dependencies lazy here
    //This means that dp will instantiate when we call .get() on it. Not when we pass db as parameter
    class Callback @Inject constructor(
            //dagger wouldn't instantiate here db.
            //TODO maybe change to Provider<TaskDao>
            private val database: Provider<TaskDatabase>,
            @ApplicationScope private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)

            //database will instantiate in this point
            val taskDao = database.get().taskDao()

            scope.launch {
                taskDao.insert(Task(name = "Wash the dishes"))
                taskDao.insert(Task(name = "Task2", important = true))
                taskDao.insert(Task(name = "Task3", completed = true))
                taskDao.insert(Task(name = "Task4", completed = true))
            }
        }
    }
}