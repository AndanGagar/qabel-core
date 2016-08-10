package de.qabel.core.index

import org.junit.Test
import org.junit.Assert.*

class SearchEndpointTest {
    private val server = IndexHTTPLocation("http://localhost:9698")
    private val search = SearchEndpointImpl(server, createGson())

    @Test(expected = IllegalArgumentException::class)
    fun testBuildSearchRequestEmpty() {
        search.buildRequest(mapOf())
    }

    @Test
    fun testBuildSearchRequestSingle() {
        val attributes = mapOf(Pair(FieldType.EMAIL, "test@example.net"))
        val request = search.buildRequest(attributes)
        assertEquals("email=test@example.net", request.uri.query)
    }

    @Test
    fun testParseSearchResponseKaput() {
        val brokenResponses = listOf(
            "",
            "{", /* that will stay with you all day */
            "{}",
            "{\"1identity\": []}"
        )
        fun assertBrokenResponse(json: String) {
            try {
                search.parseResponse(json, dummyStatusLine())
                fail("Did not raise MalformedResponseException, input:\n" + json)
            } catch (e: MalformedResponseException) {
                return
            }
        }
        for (brokenResponse in brokenResponses) {
            assertBrokenResponse(brokenResponse)
        }
    }

    @Test
    fun testParseSearchResponseIgnoresBrokenIdentities() {
        val responseWithBrokenIdentity = listOf(
            // public_key has incorrect length
            """
            {"identities": [{
               "public_key": "0f82b0018d1140a37b9cf3a4570bbdd415c8bbbcfc7efe7ef8aa912ce37605",
               "drop_url": "http://example.net/somewhere/abcdefghijklmnopqrstuvwxyzabcdefghijklmnopo",
               "alias": "1234"
            }]}
            """,

            // drop_url is malformed (drop ID too short)
            """
            {"identities": [{
               "public_key": "0f82b0018d1140a37b9cf3a4570bbdd415c8bbbcfc7efe7ef8aa912ce3760520",
               "drop_url": "http://example.net/somewhere/abcdefghijwxyzabcdefghijklmnopo",
               "alias": "1234"
            }]}
            """,

            // that's not even an URL
            """
            {"identities": [{
               "public_key": "0f82b0018d1140a37b9cf3a4570bbdd415c8bbbcfc7efe7ef8aa912ce3760520",
               "drop_url": "hänschen klein ging allein, vertraut input aus dem internet. The End.",
               "alias": "1234"
            }]}
            """,

            // identity without alias
            """
            {"identities": [{
                "public_key": "0f82b0018d1140a37b9cf3a4570bbdd415c8bbbcfc7efe7ef8aa912ce3760520",
                "drop_url": "http://example.net/somewhere/abcdefghijklmnopqrstuvwxyzabcdefghijklmnopo"
            }]}
            """
        )
        fun assertIgnoresBrokenIdentity(json: String) {
            val result = search.parseResponse(json, dummyStatusLine())
            assertEquals(0, result.size)
        }
        for (brokenResponse in responseWithBrokenIdentity) {
            assertIgnoresBrokenIdentity(brokenResponse)
        }
    }

    @Test
    fun testParseSearchResponseEmpty() {
        val json = "{\"identities\": []}"
        val result = search.parseResponse(json, dummyStatusLine())
        assertEquals(0, result.size)
    }

    @Test
    fun testParseSearchResponse() {
        val json =
            """
            {"identities": [{
               "public_key": "0f82b0018d1140a37b9cf3a4570bbdd415c8bbbcfc7efe7ef8aa912ce3760520",
               "drop_url": "http://example.net/somewhere/abcdefghijklmnopqrstuvwxyzabcdefghijklmnopo",
               "alias": "1234"
            }]}
            """
        val result = search.parseResponse(json, dummyStatusLine())
        assertEquals(1, result.size)
        val identity = result[0]
        assertEquals("1234", identity.alias)
        assertEquals("http://example.net/somewhere/abcdefghijklmnopqrstuvwxyzabcdefghijklmnopo", identity.dropUrl.uri.toASCIIString())
        assertEquals("0f82b0018d1140a37b9cf3a4570bbdd415c8bbbcfc7efe7ef8aa912ce3760520", identity.publicKey.readableKeyIdentifier)
    }

    @Test
    fun testParseSearchResponseUnknownFields() {
        val json =
            """
            {"identities": [{
               "public_key": "0f82b0018d1140a37b9cf3a4570bbdd415c8bbbcfc7efe7ef8aa912ce3760520",
               "drop_url": "http://example.net/somewhere/abcdefghijklmnopqrstuvwxyzabcdefghijklmnopo",
               "alias": "1234",
               "field_from_the_future": "I'm back"
            }]}
            """
        val result = search.parseResponse(json, dummyStatusLine())
        assertEquals(1, result.size)
        val identity = result[0]
        assertEquals("1234", identity.alias)
        assertEquals("http://example.net/somewhere/abcdefghijklmnopqrstuvwxyzabcdefghijklmnopo", identity.dropUrl.uri.toASCIIString())
        assertEquals("0f82b0018d1140a37b9cf3a4570bbdd415c8bbbcfc7efe7ef8aa912ce3760520", identity.publicKey.readableKeyIdentifier)
    }
}
