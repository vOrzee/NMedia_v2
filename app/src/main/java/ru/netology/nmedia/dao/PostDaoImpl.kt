//package ru.netology.nmedia.dao
//
//import android.content.ContentValues
//import android.database.Cursor
//import android.database.sqlite.SQLiteDatabase
//import android.util.Log
//import ru.netology.nmedia.dto.Post
//
//class PostDaoImpl(private val db: SQLiteDatabase) : PostDao {
//    companion object {
//        private const val TAG = "PostDaoImpl"
//
//        val DDL = """
//        CREATE TABLE ${PostColumns.TABLE} (
//            ${PostColumns.COLUMN_ID} INTEGER PRIMARY KEY AUTOINCREMENT,
//            ${PostColumns.COLUMN_AUTHOR} TEXT NOT NULL,
//            ${PostColumns.COLUMN_CONTENT} TEXT NOT NULL,
//            ${PostColumns.COLUMN_PUBLISHED} TEXT NOT NULL,
//            ${PostColumns.COLUMN_LIKED_BY_ME} BOOLEAN NOT NULL DEFAULT 0,
//            ${PostColumns.COLUMN_LIKES} INTEGER NOT NULL DEFAULT 0,
//            ${PostColumns.COLUMN_SHARE} INTEGER NOT NULL
//        );
//        """.trimIndent()
//    }
//
//    object PostColumns {
//        const val TABLE = "posts"
//        const val COLUMN_ID = "id"
//        const val COLUMN_AUTHOR = "author"
//        const val COLUMN_CONTENT = "content"
//        const val COLUMN_PUBLISHED = "published"
//        const val COLUMN_LIKED_BY_ME = "likedByMe"
//        const val COLUMN_LIKES = "likes"
//        const val COLUMN_SHARE = "share"
//        val ALL_COLUMNS = arrayOf(
//            COLUMN_ID,
//            COLUMN_AUTHOR,
//            COLUMN_CONTENT,
//            COLUMN_PUBLISHED,
//            COLUMN_LIKED_BY_ME,
//            COLUMN_LIKES,
//            COLUMN_SHARE
//        )
//    }
//
//    override fun getAll(): List<Post> {
//        val posts = mutableListOf<Post>()
//        db.query(
//            PostColumns.TABLE,
//            PostColumns.ALL_COLUMNS,
//            null,
//            null,
//            null,
//            null,
//            "${PostColumns.COLUMN_ID} DESC"
//        ).use {
//            while (it.moveToNext()) {
//                posts.add(map(it))
//            }
//        }
//        return posts
//    }
//
//    override fun save(post: Post): Post {
//        val values = ContentValues().apply {
//            put(PostColumns.COLUMN_AUTHOR, "Me")
//            put(PostColumns.COLUMN_CONTENT, post.content)
//            put(PostColumns.COLUMN_PUBLISHED, "now")
//            put(PostColumns.COLUMN_SHARE, post.shareById)
//        }
//        val id = if (post.id != 0L) {
//            db.update(
//                PostColumns.TABLE,
//                values,
//                "${PostColumns.COLUMN_ID} = ?",
//                arrayOf(post.id.toString()),
//            )
//            post.id
//        } else {
//            val newId = db.insert(PostColumns.TABLE, null, values)
//            if (newId == -1L) throw android.database.SQLException("Error inserting post")
//            newId
//        }
//        db.query(
//            PostColumns.TABLE,
//            PostColumns.ALL_COLUMNS,
//            "${PostColumns.COLUMN_ID} = ?",
//            arrayOf(id.toString()),
//            null,
//            null,
//            null,
//        ).use {
//            if (!it.moveToNext()) throw android.database.SQLException("Post not found after save")
//            return map(it)
//        }
//    }
//
//    override fun likeById(id: Long) {
//        try {
//            db.execSQL(
//                """
//           UPDATE posts SET
//               likes = likes + CASE WHEN likedByMe THEN -1 ELSE 1 END,
//               likedByMe = CASE WHEN likedByMe THEN 0 ELSE 1 END
//           WHERE id = ?;
//        """.trimIndent(), arrayOf(id)
//            )
//        } catch (e: Exception) {
//            Log.e(TAG, "Error in likeById(id=$id)", e)
//            throw e
//        }
//    }
//
//    override fun removeById(id: Long) {
//        db.delete(
//            PostColumns.TABLE,
//            "${PostColumns.COLUMN_ID} = ?",
//            arrayOf(id.toString())
//        )
//    }
//
//    override fun shareById(id: Long) {
//        try {
//            db.execSQL(
//                """
//           UPDATE posts SET
//               share = share + 1
//           WHERE id = ?;
//        """.trimIndent(), arrayOf(id.toString())
//            )
//        } catch (e: Exception) {
//            Log.e(TAG, "Error in shareById(id=$id)", e)
//            throw e
//        }
//    }
//
//    private fun map(cursor: Cursor): Post {
//        with(cursor) {
//            return Post(
//                id = getLong(getColumnIndexOrThrow(PostColumns.COLUMN_ID)),
//                author = getString(getColumnIndexOrThrow(PostColumns.COLUMN_AUTHOR)),
//                content = getString(getColumnIndexOrThrow(PostColumns.COLUMN_CONTENT)),
//                published = getString(getColumnIndexOrThrow(PostColumns.COLUMN_PUBLISHED)),
//                likedByMe = getInt(getColumnIndexOrThrow(PostColumns.COLUMN_LIKED_BY_ME)) != 0,
//                likes = getInt(getColumnIndexOrThrow(PostColumns.COLUMN_LIKES)),
//                shareById = getInt(getColumnIndexOrThrow(PostColumns.COLUMN_SHARE)),
//                videoUrl = "",
//                views = 0,
//            )
//        }
//    }
//
//}
