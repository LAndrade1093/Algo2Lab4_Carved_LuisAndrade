import java.awt.Color;
import edu.neumont.ui.Picture;

public class SeamCarver 
{
	Picture picture;
	enum SeamType {vertical, horizontal};

	public SeamCarver(Picture pic)
	{
		picture = pic;
	}

	/*
	 * Returns the current image
	 */
	public Picture getPicture()
	{
		return picture;
	}

	public int width()
	{
		return picture.width();
	}

	public int height()
	{
		return picture.height();
	}

	/*
	 * Calculates the energy of a pixel at (x,y)
	 */
	public double energy(int x, int y)
	{
		double energy = 0;
		if(outsideImageRange(x, y))
		{
			throw new IndexOutOfBoundsException("The coordinates (" + x + "," + y + ") is outside the range of the image.");
		}
		else
		{
			Color leftPixel = getLeftPixel(x, y);
			Color rightPixel = getRightPixel(x, y);
			double xR = leftPixel.getRed() - rightPixel.getRed();
			double xG = leftPixel.getGreen() - rightPixel.getGreen();
			double xB = leftPixel.getBlue() - rightPixel.getBlue();
			double changeInX = (xR * xR) + (xG * xG) + (xB * xB);

			Color topPixel = getTopPixel(x, y);
			Color bottomPixel = getBottomPixel(x, y);
			double yR = topPixel.getRed() - bottomPixel.getRed();
			double yG = topPixel.getGreen() - bottomPixel.getGreen();
			double yB = topPixel.getBlue() - bottomPixel.getBlue();
			double changeInY = (yR * yR) + (yG * yG) + (yB * yB);

			energy = changeInX + changeInY;
		}

		return energy;
	}



	/*
	 * Finds the sequence of indices for a horizontal seam
	 * Each index in the generated path array represents a column (x-value) in the picture
	 * The value in the index is the row (y-value) index within the column that will be removed
	 */
	public int[] findHorizontalSeam()
	{
		//The energy calculations (or weights) of each pixel plus the energy of the 
		//parent pixel in the previous column or row with the lowest energy
		double[][] energyPathWeights = new double[width()][height()];

		//Each vertex holds a pointer to the pixel next to it that had the lowest energy
		int[][] pathPointers = new int[width()][height()];

		//Used to determine which path has the minimum total energy
		int minIndex = -1;
		double lowestEnergyWeight = 0;

		for(int x = 0; x < width(); x++)
		{
			for(int y = 0; y < height(); y++)
			{
				if(x == 0)
				{
					energyPathWeights[x][y] = energy(x, y);
					pathPointers[x][y] = -1;
				}
				else
				{
					double topParentPixel = (y == 0) ? energyPathWeights[x-1][0] : energyPathWeights[x-1][y-1];
					double parentPixel = energyPathWeights[x-1][y];
					double bottomParentPixel = (y == height() - 1) ? energyPathWeights[x-1][height() - 1] : energyPathWeights[x-1][y+1];

					int b = 0;
					if(parentPixel <= bottomParentPixel && parentPixel <= topParentPixel)
					{
						energyPathWeights[x][y] = energy(x, y) + parentPixel;
						b = pathPointers[x][y] = y;
					}
					else if(bottomParentPixel <= parentPixel && bottomParentPixel <= topParentPixel)
					{
						energyPathWeights[x][y] = energy(x, y) + bottomParentPixel;
						b = pathPointers[x][y] = (y == height() - 1) ? height() - 1 : y+1;
					}
					else if(topParentPixel <= parentPixel && topParentPixel <= bottomParentPixel)
					{
						energyPathWeights[x][y] = energy(x, y) + topParentPixel;
						b = pathPointers[x][y] = (y == 0) ? 0 : y-1;
					}
					else
					{
						energyPathWeights[x][y] = energy(x, y) + parentPixel;
						b = pathPointers[x][y] = y;
					}
				}

				if(x == width() - 1)
				{
					if(minIndex == -1 || energyPathWeights[x][y] < lowestEnergyWeight)
					{
						minIndex = y;
						lowestEnergyWeight = energyPathWeights[x][minIndex];
					}
				}
			}
		}

		return buildSeamPath(minIndex, pathPointers, SeamType.horizontal);		
	}

	/*
	 * Finds the sequence of indices for a vertical seam
	 * Each index in the generated path array represents a row (y-value) in the picture
	 * The value in the index is the column (x-value) index within the row that will be removed
	 */
	public int[] findVerticalSeam()
	{
		double[][] energyPathWeights = new double[width()][height()];
		int[][] pathPointers = new int[width()][height()];

		int minIndex = -1;
		double lowestEnergyWeight = 0;

		for(int y = 0; y < height(); y++)
		{
			for(int x = 0; x < width(); x++)
			{
				if(y == 0)
				{
					energyPathWeights[x][y] = energy(x, y);
					pathPointers[x][y] = -1;
				}
				else
				{
					double leftParentPixel = (x == 0) ? energyPathWeights[0][y-1] : energyPathWeights[x-1][y-1];
					double parentPixel = energyPathWeights[x][y-1];
					double rightParentPixel = (x == width() - 1) ? energyPathWeights[width() - 1][y-1] : energyPathWeights[x+1][y-1];

					if(parentPixel <= leftParentPixel && parentPixel <= rightParentPixel)
					{
						energyPathWeights[x][y] = energy(x, y) + parentPixel;
						pathPointers[x][y] = x;
					}
					else if(leftParentPixel <= parentPixel && leftParentPixel <= rightParentPixel)
					{
						energyPathWeights[x][y] = energy(x, y) + leftParentPixel;
						pathPointers[x][y] = (x == 0) ? 0 : x-1;
					}
					else if(rightParentPixel <= parentPixel && rightParentPixel <= leftParentPixel)
					{
						energyPathWeights[x][y] = energy(x, y) + rightParentPixel;
						pathPointers[x][y] = (x == width() - 1) ? width() - 1 : x+1;
					}
					else
					{
						energyPathWeights[x][y] = energy(x, y) + parentPixel;
						pathPointers[x][y] = x;
					}
				}

				if(y == height() - 1)
				{
					if(minIndex == -1 || energyPathWeights[x][y] < lowestEnergyWeight)
					{
						minIndex = x;
						lowestEnergyWeight = energyPathWeights[minIndex][y];
					}
				}
			}
		}

		return buildSeamPath(minIndex, pathPointers, SeamType.vertical);
	}

	public void removeHorizontalSeam(int[] indices)
	{
		if(width() <= 1)
		{
			throw new IllegalArgumentException("Cannot remove horizontal seam from an image of width 1 or less.");
		}
		else if(indices.length < width())
		{
			throw new IllegalArgumentException("The length of the horizontal seam is too short for the image's width.");
		}
		else if(indices.length > width())
		{
			throw new IllegalArgumentException("The length of the horizontal seam is too long for the image's width.");
		}
		else
		{
			Picture resizedPicture = new Picture(width(), height() - 1);
			for(int x = 0; x < width(); x++)
			{
				int currentRowEntry = indices[x];
				if(outsideImageRange(x, currentRowEntry))
				{
					throw new IndexOutOfBoundsException("The coordinates (" + x + "," + currentRowEntry + ") are outside the range of the image.");
				}
				else
				{
					int previousEntry = (x == 0) ? currentRowEntry : indices[x-1];
					if(seamIsConnected(currentRowEntry, previousEntry))
					{
						for(int y = 0; y < height() - 1; y++)
						{
							Color pixelRGB = (y < currentRowEntry) ? picture.get(x, y) : picture.get(x, y+1);
							resizedPicture.set(x, y, pixelRGB);
						}
						previousEntry = currentRowEntry;
					}
					else
					{
						throw new IllegalArgumentException("The seam is disconnected. This is not a valid seam.");
					}
				}
			}

			picture = resizedPicture;
		}
	}

	public void removeVerticalSeam(int[] indices)
	{
		if(height() <= 1)
		{
			throw new IllegalArgumentException("Cannot remove vertical seam from an image of height 1 or less.");
		}
		else if(indices.length < height())
		{
			throw new IllegalArgumentException("The length of the vertical seam is too short for the image's height.");
		}
		else if(indices.length > height())
		{
			throw new IllegalArgumentException("The length of the vertical seam is too long for the image's height.");
		}
		else
		{
			Picture resizedPicture = new Picture(width() - 1, height());
			for(int y = 0; y < height(); y++)
			{
				int currentColumnEntry = indices[y];
				if(outsideImageRange(currentColumnEntry, y))
				{
					throw new IndexOutOfBoundsException("The coordinates (" + currentColumnEntry + "," + y + ") are outside the range of the image.");
				}
				else
				{
					int previousEntry = (y == 0) ? currentColumnEntry : indices[y-1];
					if(seamIsConnected(currentColumnEntry, previousEntry))
					{
						for(int x = 0; x < width() - 1; x++)
						{
							Color pixelRGB = (x < currentColumnEntry) ? picture.get(x, y) : picture.get(x+1, y);
							resizedPicture.set(x, y, pixelRGB);
						}
					}
					else
					{
						throw new IllegalArgumentException("The seam is disconnected. This is not a valid seam.");
					}
				}
			}

			picture = resizedPicture;
		}
	}

	private int[] buildSeamPath(int minIndex, int[][] pathPointers, SeamType seam)
	{
		int[] path = null;
		int NO_PARENT_POINTER = -1;

		if(seam == SeamType.horizontal)
		{
			int x = width() - 1;
			int y = minIndex;
			path = new int[width()];
			path[x] = y;
			while(pathPointers[x][y] != NO_PARENT_POINTER)
			{
				path[x - 1] = pathPointers[x][y];
				y = path[x - 1];
				x--;
			}
		}
		else
		{
			int x = minIndex;
			int y = height() - 1;
			path = new int[height()];
			path[y] = x;
			while(pathPointers[x][y] != NO_PARENT_POINTER)
			{
				path[y - 1] = pathPointers[x][y];
				x = path[y - 1];
				y--;
			}
		}

		return path;
	}




	private boolean outsideImageRange(int x, int y)
	{
		return (x >= width() || x < 0 || y >= height() || y < 0);
	}

	private boolean seamIsConnected(int currentEntry, int previousEntry)
	{
		int lowerBound = previousEntry - 1;
		int upperBound = previousEntry + 1;
		return (currentEntry >= lowerBound && currentEntry <= upperBound);
	}

	private Color getLeftPixel(int x, int y)
	{
		if(x == 0)
		{
			return picture.get(width()-1, y);
		}
		return picture.get(x-1, y);
	}

	private Color getRightPixel(int x, int y)
	{
		if(x == width()-1)
		{
			return picture.get(0, y);
		}
		return picture.get(x+1, y);
	}

	private Color getTopPixel(int x, int y)
	{
		if(y == 0)
		{
			return picture.get(x, height()-1);
		}
		return picture.get(x, y-1);
	}

	private Color getBottomPixel(int x, int y)
	{
		if(y == height()-1)
		{
			return picture.get(x, 0);
		}
		return picture.get(x, y+1);
	}

}