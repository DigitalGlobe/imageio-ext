/*
 *    ImageI/O-Ext - OpenSource Java Image translation Library
 *    http://www.geo-solutions.it/
 *    http://java.net/projects/imageio-ext/
 *    (C) 2007 - 2009, GeoSolutions
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    either version 3 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package it.geosolutions.imageio.plugins.arcgrid;

import it.geosolutions.imageio.gdalframework.AbstractGDALTest;
import it.geosolutions.imageio.gdalframework.Viewer;
import it.geosolutions.resources.TestData;

import java.awt.Rectangle;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.media.jai.JAI;
import javax.media.jai.ParameterBlockJAI;
import javax.media.jai.RenderedOp;

import org.junit.Assert;
import org.junit.Test;

/**
 * Testing reading capabilities for {@link ArcGridImageReader} leveraging on
 * JAI.
 * 
 * @author Simone Giannecchini, GeoSolutions.
 * @author Daniele Romagnoli, GeoSolutions.
 * 
 */
public class ArcGridReadTest extends AbstractGDALTest {
	public ArcGridReadTest() {
		super();
	}

	/**
	 * Simple test read through JAI - ImageIO
	 * 
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	@org.junit.Test
	public void readJAI() throws FileNotFoundException, IOException {
		if (!isGDALAvailable) {
			return;
		}
		final ParameterBlockJAI pbjImageRead;
		final String fileName = "095b_dem_90m.asc";
		TestData.unzipFile(this, "arcgrid.zip");
		final File file = TestData.file(this, fileName);
		pbjImageRead = new ParameterBlockJAI("ImageRead");
		pbjImageRead.setParameter("Input", file);
		RenderedOp image = JAI.create("ImageRead", pbjImageRead);
		if (TestData.isInteractiveTest())
			Viewer.visualizeAllInformation(image, fileName);
		else
			image.getTiles();
		Assert.assertEquals(351, image.getWidth());
		Assert.assertEquals(350, image.getHeight());
	}

	/**
	 * Simple test read through ImageIO
	 * 
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	@Test
	public void readImageIO() throws FileNotFoundException, IOException {
		if (!isGDALAvailable) {
			return;
		}
		final File file = TestData.file(this, "095b_dem_90m.asc");

		// //
		//
		// Try to get a reader for this raster data
		//
		// //
		final Iterator<ImageReader> it = ImageIO.getImageReaders(file);
		Assert.assertTrue(it.hasNext());

		// //
		//
		// read some data from it using subsampling
		//
		// //
		final ImageReader reader = (ImageReader) it.next();
		Assert.assertTrue(reader instanceof ArcGridImageReader);
		ImageReadParam rp = reader.getDefaultReadParam();
		rp.setSourceSubsampling(2, 2, 0, 0);
		reader.setInput(file);
		RenderedImage image = reader.read(0, rp);
		if (TestData.isInteractiveTest())
			Viewer.visualizeAllInformation(image, "subsample read " + file.getName());
		reader.reset();

		Assert.assertEquals((int) (reader.getWidth(0) / 2.0 + 0.5), image.getWidth());
		Assert.assertEquals((int) (reader.getHeight(0) / 2.0 + 0.5), image.getHeight());

		// //
		//
		// read some data from it using sourceregion
		//
		// //
		Assert.assertTrue(reader instanceof ArcGridImageReader);
		rp = reader.getDefaultReadParam();
		rp.setSourceRegion(new Rectangle(0, 0, 60, 42));
		reader.setInput(file);
		image = reader.read(0, rp);
		if (TestData.isInteractiveTest())
			Viewer.visualizeAllInformation(image, "subsample read " + file.getName());
		reader.reset();

		Assert.assertEquals(60, image.getWidth());
		Assert.assertEquals(42, image.getHeight());

		reader.dispose();
	}
}