/*
 * Copyright 2013 CodeSlap
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.codeslap.test.persistence;

import org.junit.Assert;
import org.junit.Test;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author cristian
 */
public class DeleteTest extends SqliteTest {
  @Test
  public void testDelete() {
    // let's first insert a collection of data
    List<ExampleAutoincrement> collection = new ArrayList<ExampleAutoincrement>();
    Random random = new Random();
    for (int i = 0; i < 100; i++) {
      ExampleAutoincrement foo = new ExampleAutoincrement();
      foo.name = "Foo Bar " + random.nextInt();
      foo.number = random.nextInt();
      foo.decimal = random.nextFloat();
      foo.bool = random.nextBoolean();
      foo.blob = foo.name.getBytes();
      collection.add(foo);
    }
    mAdapter.storeCollection(collection, null);

    // now let's delete some data!
    int deleted = mAdapter.delete(collection.get(0));
    assertEquals(1, deleted);

    ExampleAutoincrement foo = collection.get(1);
    foo.name = null;
    assertTrue(mAdapter.delete(foo) > 0);
    foo.name = "Something";
    assertTrue(mAdapter.delete(foo) == 0);

    int count = mAdapter.count(ExampleAutoincrement.class);
    assertTrue(count > 0);

    deleted = mAdapter.delete(ExampleAutoincrement.class, "name LIKE ?", new String[]{"Foo%"});
    assertTrue(deleted > 0);

    count = mAdapter.count(ExampleAutoincrement.class);
    assertEquals(0, count);
  }

  @Test
  public void testDeleteAnnotations() {
    // let's first insert a collection of data
    List<AnnotationAutoincrement> collection = new ArrayList<AnnotationAutoincrement>();
    Random random = new Random();
    for (int i = 0; i < 100; i++) {
      AnnotationAutoincrement foo = new AnnotationAutoincrement();
      foo.name = "Foo Bar " + random.nextInt();
      foo.number = random.nextInt();
      foo.decimal = random.nextFloat();
      foo.bool = random.nextBoolean();
      collection.add(foo);
    }
    mAdapter.storeCollection(collection, null);

    // now let's delete some data!
    int deleted = mAdapter.delete(collection.get(0));
    assertEquals(1, deleted);

    AnnotationAutoincrement foo = collection.get(1);
    foo.name = null;
    assertTrue(mAdapter.delete(foo) > 0);

    int count = mAdapter.count(AnnotationAutoincrement.class);
    assertTrue(count > 0);

    deleted = mAdapter.delete(AnnotationAutoincrement.class, "char_sequence LIKE ?", new String[]{"Foo%"});
    assertTrue(deleted > 0);

    count = mAdapter.count(AnnotationAutoincrement.class);
    assertEquals(0, count);
  }

  @Test
  public void testDeleteHasMany() {
    God jesus = new God();
    jesus.name = "Jesus";
    God thor = new God();
    thor.name = "Thor";

    PolyTheist dummy = new PolyTheist();
    dummy.gods = Arrays.asList(thor, jesus);

    mAdapter.store(dummy);

    Assert.assertEquals(1, mAdapter.findAll(PolyTheist.class).size());
    Assert.assertEquals(2, mAdapter.findAll(God.class).size());

    mAdapter.delete(PolyTheist.class, null, null);

    Assert.assertEquals(0, mAdapter.findAll(PolyTheist.class).size());
    Assert.assertEquals(2, mAdapter.findAll(God.class).size());
  }

  @Test
  public void testDeleteHasManyOnCascade() {
    God jesus = new God();
    jesus.name = "Jesus";
    God thor = new God();
    thor.name = "Thor";

    PolyTheist dummy = new PolyTheist();
    dummy.gods = Arrays.asList(thor, jesus);

    mAdapter.store(dummy);

    Assert.assertEquals(1, mAdapter.findAll(PolyTheist.class).size());
    Assert.assertEquals(2, mAdapter.findAll(God.class).size());

    mAdapter.delete(PolyTheist.class, null, null, true);

    Assert.assertEquals(0, mAdapter.findAll(PolyTheist.class).size());
    Assert.assertEquals(0, mAdapter.findAll(God.class).size());
  }

  @Test
  public void testDeleteManyToMany() {
    Book puta = new Book();
    puta.id = 1;
    puta.name = "La puta de Babilonia";

    Book vida = new Book();
    vida.id = 2;
    vida.name = "El Don de la Vida";

    Author author = new Author();
    author.name = "Fernando Vallejo";
    author.books = Arrays.asList(puta, vida);

    Author william = new Author();
    william.name = "William Ospina";
    william.books = Arrays.asList(puta);

    mAdapter.store(author);
    mAdapter.store(william);

    Assert.assertEquals(2, mAdapter.findAll(Author.class).size());
    Assert.assertEquals(2, mAdapter.findAll(Book.class).size());

    mAdapter.delete(Author.class, "name LIKE ?", new String[]{"Fernando Vallejo"});

    Assert.assertEquals(1, mAdapter.findAll(Author.class).size());
    Assert.assertEquals(2, mAdapter.findAll(Book.class).size());

    mAdapter.delete(Author.class, "name LIKE ?", new String[]{"William Ospina"});

    Assert.assertEquals(0, mAdapter.findAll(Author.class).size());
    Assert.assertEquals(2, mAdapter.findAll(Book.class).size());
  }

  @Test
  public void testDeleteManyToManyOnCascade() {
    Book puta = new Book();
    puta.id = 1;
    puta.name = "La puta de Babilonia";

    Book vida = new Book();
    vida.id = 2;
    vida.name = "El Don de la Vida";

    Author author = new Author();
    author.name = "Fernando Vallejo";
    author.books = Arrays.asList(puta, vida);

    Author william = new Author();
    william.name = "William Ospina";
    william.books = Arrays.asList(puta);

    mAdapter.store(author);
    mAdapter.store(william);

    Assert.assertEquals(2, mAdapter.findAll(Author.class).size());
    Assert.assertEquals(2, mAdapter.findAll(Book.class).size());

    mAdapter.delete(Author.class, "name LIKE ?", new String[]{"Fernando Vallejo"}, true);

    Assert.assertEquals(1, mAdapter.findAll(Author.class).size());
    Assert.assertEquals(1, mAdapter.findAll(Book.class).size());

    mAdapter.delete(Author.class, "name LIKE ?", new String[]{"William Ospina"}, true);

    Assert.assertEquals(0, mAdapter.findAll(Author.class).size());
    Assert.assertEquals(0, mAdapter.findAll(Book.class).size());
  }
}
