int Image::AddRandomNoise(const float& noise,Image& outputImage) const
{
	if(noise >= 0 && noise <= 1){

		// same image if 0 noise to be added 
		if (noise == 0)
			return 1;

		float rChange = 0, gChange = 0, bChange = 0, newR = 0, newG = 0, newB = 0;

		srand((unsigned int)time(NULL));

		for (int y = 0; y < outputImage.height(); y++) {
			for (int x = 0; x < outputImage.width(); x++) {

				Pixel &p = outputImage.pixel(x, y);

				// rand() % a generates a number between 0 and a-1, inclusively
				rChange = noise * (rand() % 256) * (rand() % 2 == 0 ? -1 : 1);
				gChange = noise * (rand() % 256) * (rand() % 2 == 0 ? -1 : 1);
				bChange = noise * (rand() % 256) * (rand() % 2 == 0 ? -1 : 1);

				newR = p.r + rChange;
				newG = p.g + gChange;
				newB = p.b + bChange;

				newR = newR > 255 ? 255 : newR;
				newG = newG > 255 ? 255 : newG;
				newB = newB > 255 ? 255 : newB;

				newR = newR < 0 ? 0 : newR;
				newG = newG < 0 ? 0 : newG;
				newB = newB < 0 ? 0 : newB;

				p.r = newR;
				p.g = newG;
				p.b = newB;
			}
		}
		return 1;
	}
	return 0;
}

int Image::Brighten(const float& brightness,Image& outputImage) const
{
	if(brightness >= 0){
		// same image if brightness scale is 1
		if (brightness == 1)
			return 1;

		float newR = 0, newG = 0, newB = 0;

		for (int y = 0; y < outputImage.height(); y++) {
			for (int x = 0; x < outputImage.width(); x++) {

				Pixel &p = outputImage.pixel(x, y);	

				newR = p.r * brightness;
				newG = p.g * brightness;
				newB = p.b * brightness;

				newR = newR > 255 ? 255 : newR;
				newG = newG > 255 ? 255 : newG;
				newB = newB > 255 ? 255 : newB;

				newR = newR < 0 ? 0 : newR;
				newG = newG < 0 ? 0 : newG;
				newB = newB < 0 ? 0 : newB;

				p.r = (unsigned char) newR;
				p.g = (unsigned char) newG;
				p.b = (unsigned char) newB;
			}
		}
		return 1;
	}
    return 0;
}

int Image::Luminance(Image& outputImage) const
{
	float luminance = 0;

	for (int y = 0; y < outputImage.height(); y++) {
			for (int x = 0; x < outputImage.width(); x++) {

				Pixel &p = outputImage.pixel(x, y);	

				luminance = 0.30 * p.r + 0.59 * p.g + 0.11 * p.b;
				luminance = luminance > 255 ? 255 : luminance;
				luminance = luminance < 0 ? 0 : luminance;

				p.r = (unsigned char) luminance;
				p.g = (unsigned char) luminance;
				p.b = (unsigned char) luminance;
			}
	}
	return 1;
}

int Image::Contrast(const float& contrast,Image& outputImage) const
{
	if (contrast >= 0){
		
		if (contrast == 1)
			return 1;

		float average_luminance = 0, count = 0;

		for (int y = 0; y < outputImage.height(); y++) {
			for (int x = 0; x < outputImage.width(); x++) {

					count++;

					Pixel &p = outputImage.pixel(x, y);	

					average_luminance += 0.30 * p.r + 0.59 * p.g + 0.11 * p.b;
				}
		}

		average_luminance /= count;

		float newR = 0, newG = 0, newB = 0;

		for (int n = 0; n < outputImage.height(); n++) {
			for (int m = 0; m < outputImage.width(); m++) {

	            Pixel& p = outputImage.pixel(m, n);

	            newR = (p.r - average_luminance) * contrast  + (255 - average_luminance);
	            newG = (p.g - average_luminance) * contrast  + (255 - average_luminance);
				newB = (p.b - average_luminance) * contrast  + (255 - average_luminance);

				newR = newR > 255 ? 255 : newR;
				newG = newG > 255 ? 255 : newG;
				newB = newB > 255 ? 255 : newB;

				newR = newR < 0 ? 0 : newR;
				newG = newG < 0 ? 0 : newG;
				newB = newB < 0 ? 0 : newB;
	            
	            p.r = (unsigned char) newR;
				p.g = (unsigned char) newG;
				p.b = (unsigned char) newB;
	        }
	    }
	    return 1;
	}
	return 0;
}

int Image::Saturate(const float& saturation,Image& outputImage) const
{
	if (saturation >= 0) {

		float newR = 0, newG = 0, newB = 0;

		for (int y = 0; y < outputImage.height(); y++) {
			for (int x = 0; x < outputImage.width(); x++) {

                Pixel& p = outputImage.pixel(x, y);

                float luminance = 0.30 * p.r + 0.59 * p.g + 0.11 * p.b;

                newR = (p.r - luminance) * saturation + luminance;
                newG = (p.g - luminance) * saturation + luminance;
                newB = (p.b - luminance) * saturation + luminance;

                newR = newR > 255 ? 255 : newR;
				newG = newG > 255 ? 255 : newG;
				newB = newB > 255 ? 255 : newB;

				newR = newR < 0 ? 0 : newR;
				newG = newG < 0 ? 0 : newG;
				newB = newB < 0 ? 0 : newB;

                p.r = (unsigned char) newR;
                p.g = (unsigned char) newG;
                p.b = (unsigned char) newB;
            }
        }
		return 1;
	}

	return 0;
}

int Image::Blur(Image& outputImage) const
{
	unsigned short filter[3][3] = { 1, 2, 1,
									2, 4, 2,
									1, 2, 1, };

	Image blurredImage;
	blurredImage.setSize(outputImage.width(), outputImage.height());

	for (int y = 0; y < outputImage.height(); y++) {
		for (int x = 0; x < outputImage.width(); x++) {
			Pixel &pixel = outputImage.pixel(x, y);
			
			unsigned short totalPixels = 4;

			unsigned int newR = pixel.r * filter[1][1];
			unsigned int newG = pixel.g * filter[1][1];
			unsigned int newB = pixel.b * filter[1][1];
			
			if (x > 0 && y > 0) {
				newR += outputImage.pixel(x - 1, y - 1).r * filter[0][0];
				newG += outputImage.pixel(x - 1, y - 1).g * filter[0][0];
				newB += outputImage.pixel(x - 1, y - 1).b * filter[0][0];
				totalPixels+= 1;
			}

			if (y > 0) {
				newR += outputImage.pixel(x, y - 1).r * filter[1][0];
				newG += outputImage.pixel(x, y - 1).g * filter[1][0];
				newB += outputImage.pixel(x, y - 1).b * filter[1][0];
				totalPixels+= 2;
			}

			if (x < outputImage.width() - 1 && y > 0) {
				newR += outputImage.pixel(x + 1, y - 1).r * filter[2][0];
				newG += outputImage.pixel(x + 1, y - 1).g * filter[2][0];
				newB += outputImage.pixel(x + 1, y - 1).b * filter[2][0];
				totalPixels+= 1;
			}

			if (x > 0) {
				newR += outputImage.pixel(x - 1, y).r * filter[0][1];
				newG += outputImage.pixel(x - 1, y).g * filter[0][1];
				newB += outputImage.pixel(x - 1, y).b * filter[0][1];
				totalPixels+= 2;
			}

			if (x < outputImage.width() - 1) {
				newR += outputImage.pixel(x + 1, y).r * filter[2][1];
				newG += outputImage.pixel(x + 1, y).g * filter[2][1];
				newB += outputImage.pixel(x + 1, y).b * filter[2][1];
				totalPixels+= 2;
			}

			if (x > 0 && y < outputImage.height() - 1) {
				newR += outputImage.pixel(x - 1, y + 1).r * filter[0][2];
				newG += outputImage.pixel(x - 1, y + 1).g * filter[0][2];
				newB += outputImage.pixel(x - 1, y + 1).b * filter[0][2];
				totalPixels+= 1;
			}

			if (y < outputImage.height() - 1) {
				newR += outputImage.pixel(x, y + 1).r * filter[1][2];
				newG += outputImage.pixel(x, y + 1).g * filter[1][2];
				newB += outputImage.pixel(x, y + 1).b * filter[1][2];
				totalPixels+= 2;
			}

			if (x < outputImage.width() -1 && y < outputImage.height() - 1) {
				newR += outputImage.pixel(x + 1, y + 1).r * filter[2][2];
				newG += outputImage.pixel(x + 1, y + 1).g * filter[2][2];
				newB += outputImage.pixel(x + 1, y + 1).b * filter[2][2];
				totalPixels+= 1;
			}

			Pixel newPixel;
			newPixel.r = newR / totalPixels;
			newPixel.g = newG / totalPixels;
			newPixel.b = newB / totalPixels;

			blurredImage.pixel(x, y) = newPixel;
		}
	}

	outputImage = blurredImage;

	return 1;
	//return 0;
}

int Image::EdgeDetect(Image& outputImage) const
{
	int filter[3][3] = { -1, -1, -1,
						 -1,  8, -1,
						 -1, -1, -1, };

	Image edgeImage;
	edgeImage.setSize(outputImage.width(), outputImage.height());

	for (int y = 0; y < outputImage.height(); y++) {
		for (int x = 0; x < outputImage.width(); x++) {
			Pixel &pixel = outputImage.pixel(x, y);
			
			int newR = pixel.r * filter[1][1];
			int newG = pixel.g * filter[1][1];
			int newB = pixel.b * filter[1][1];
			
			if (x > 0 && y > 0) {
				newR += outputImage.pixel(x - 1, y - 1).r * filter[0][0];
				newG += outputImage.pixel(x - 1, y - 1).g * filter[0][0];
				newB += outputImage.pixel(x - 1, y - 1).b * filter[0][0];
			}

			if (y > 0) {
				newR += outputImage.pixel(x, y - 1).r * filter[1][0];
				newG += outputImage.pixel(x, y - 1).g * filter[1][0];
				newB += outputImage.pixel(x, y - 1).b * filter[1][0];
			}

			if (x < outputImage.width() - 1 && y > 0) {
				newR += outputImage.pixel(x + 1, y - 1).r * filter[2][0];
				newG += outputImage.pixel(x + 1, y - 1).g * filter[2][0];
				newB += outputImage.pixel(x + 1, y - 1).b * filter[2][0];
			}

			if (x > 0) {
				newR += outputImage.pixel(x - 1, y).r * filter[0][1];
				newG += outputImage.pixel(x - 1, y).g * filter[0][1];
				newB += outputImage.pixel(x - 1, y).b * filter[0][1];
			}

			if (x < outputImage.width() - 1) {
				newR += outputImage.pixel(x + 1, y).r * filter[2][1];
				newG += outputImage.pixel(x + 1, y).g * filter[2][1];
				newB += outputImage.pixel(x + 1, y).b * filter[2][1];
			}

			if (x > 0 && y < outputImage.height() - 1) {
				newR += outputImage.pixel(x - 1, y + 1).r * filter[0][2];
				newG += outputImage.pixel(x - 1, y + 1).g * filter[0][2];
				newB += outputImage.pixel(x - 1, y + 1).b * filter[0][2];
			}

			if (y < outputImage.height() - 1) {
				newR += outputImage.pixel(x, y + 1).r * filter[1][2];
				newG += outputImage.pixel(x, y + 1).g * filter[1][2];
				newB += outputImage.pixel(x, y + 1).b * filter[1][2];
			}

			if (x < outputImage.width() -1 && y < outputImage.height() - 1) {
				newR += outputImage.pixel(x + 1, y + 1).r * filter[2][2];
				newG += outputImage.pixel(x + 1, y + 1).g * filter[2][2];
				newB += outputImage.pixel(x + 1, y + 1).b * filter[2][2];
			}

			newR = newR > 255 ? 255 : newR;
			newR = newR < 0 ? 0 : newR;

			newG = newG > 255 ? 255 : newG;
			newG = newG < 0 ? 0 : newG;

			newB = newB > 255 ? 255 : newB;
			newB = newB < 0 ? 0 : newB;

			Pixel newPixel;
			newPixel.r = newR;
			newPixel.g = newG;
			newPixel.b = newB;

			edgeImage.pixel(x, y) = newPixel;
		}
	}

	outputImage = edgeImage;
	return 1;
}

int Image::ScaleNearest(const float& scaleFactor,Image& outputImage) const
{
	// check if the scale factor is valid
	if (scaleFactor > 0) {
		Image scaledImage;

		int scaledW = outputImage.width() * scaleFactor;
		int scaledH = outputImage.height() * scaleFactor;

		scaledImage.setSize(scaledW, scaledH);

		for (int y = 0; y < scaledH; y++) {
			for (int x = 0; x < scaledW; x++) {
				float sampleX = x / scaleFactor;
				float sampleY = y / scaleFactor;

				const Pixel &sample = outputImage.NearestSample(sampleX, sampleY);
				scaledImage.pixel(x, y) = sample;
			}
		}
		
		outputImage = scaledImage;

		return 1;
	}
	return 0;
}

int Image::ScaleBilinear(const float& scaleFactor,Image& outputImage) const
{
	// check if the scale factor is valid
	if (scaleFactor > 0) {
		Image scaledImage;

		int scaledW = outputImage.width() * scaleFactor;
		int scaledH = outputImage.height() * scaleFactor;

		scaledImage.setSize(scaledW, scaledH);

		for (int y = 0; y < scaledH; y++) {
			for (int x = 0; x < scaledW; x++) {
				float sampleX = x / scaleFactor;
				float sampleY = y / scaleFactor;

				const Pixel &sample = outputImage.BilinearSample(sampleX, sampleY);
				scaledImage.pixel(x, y) = sample;
			}
		}
		
		outputImage = scaledImage;

		return 1;
	}
	return 0;
}

int Image::ScaleGaussian(const float& scaleFactor,Image& outputImage) const
{
	// check if the scale factor is valid
	if (scaleFactor > 0) {
		Image scaledImage;

		int scaledW = outputImage.width() * scaleFactor;
		int scaledH = outputImage.height() * scaleFactor;

		scaledImage.setSize(scaledW, scaledH);

		for (int y = 0; y < scaledH; y++) {
			for (int x = 0; x < scaledW; x++) {
				float sampleX = x / scaleFactor;
				float sampleY = y / scaleFactor;

				const Pixel &sample = outputImage.GaussianSample(sampleX, sampleY, 0.5 * scaleFactor, 1);
				scaledImage.pixel(x, y) = sample;
			}
		}
		
		outputImage = scaledImage;

		return 1;
	}
	return 0;
}

int Image::RotateNearest(const float& angle,Image& outputImage) const
{
	Image rotatedImage;
	float anglerad = -angle / 180 * M_PI;

	float rotationMapping[2][2] = { cos(anglerad), sin(anglerad),
						  			-sin(anglerad), cos(anglerad)};

	float warpping[2][2] = { cos(anglerad), -sin(anglerad),
						   	 sin(anglerad), cos(anglerad)};

	float minx = 0;
	float miny = 0;

	float maxx = outputImage.width();
	float maxy = outputImage.height();

	float halfw = outputImage.width()/2;
	float halfh = outputImage.height()/2;

	float w = outputImage.width();
	float h = outputImage.height();

	for (int x = 0; x < 2; x++) {
		for (int y =  0; y < 2; y++) {
			float samplex = rotationMapping[0][0] * (x*w - halfw) + rotationMapping[1][0] * (y*h - halfh);
			float sampley = rotationMapping[0][1] * (x*w - halfw) + rotationMapping[1][1] * (y*h - halfh);

			samplex += halfw;
			sampley += halfh;

			minx = samplex < minx ? samplex : minx;
			maxx = samplex > maxx ? samplex : maxx;

			miny = sampley < miny ? sampley : miny;
			maxy = sampley > maxy ? sampley : maxy;
		}
	}

	int neww = w - minx + (maxx - w);
	int newh = h - miny + (maxy - h);

	rotatedImage.setSize(neww, newh);
	Pixel sampixel;

	for (int x=0; x<neww; x++) {
		for (int y=0; y<newh; y++) {
			float samplex = warpping[0][0] * (x - halfw + minx) + warpping[1][0] * (y - halfh + miny);
			float sampley = warpping[0][1] * (x - halfw + minx) + warpping[1][1] * (y - halfh + miny);

			samplex += halfh;
			sampley += halfh;

			sampixel = outputImage.NearestSample(samplex, sampley);

			rotatedImage.pixel(x, y).r = sampixel.r;
			rotatedImage.pixel(x, y).g = sampixel.g;
			rotatedImage.pixel(x, y).b = sampixel.b;
		}
	}
	outputImage = rotatedImage;
	return 1;
}

int Image::RotateBilinear(const float& angle,Image& outputImage) const
{
	Image rotatedImage;
	float anglerad = -angle / 180 * M_PI;

	float rotationMapping[2][2] = { cos(anglerad), sin(anglerad),
						  -sin(anglerad), cos(anglerad)};

	float warpping[2][2] = { cos(anglerad), -sin(anglerad),
						   sin(anglerad), cos(anglerad)};

	float minx = 0;
	float miny = 0;

	float maxx = outputImage.width();
	float maxy = outputImage.height();

	float halfw = outputImage.width()/2;
	float halfh = outputImage.height()/2;

	float w = outputImage.width();
	float h = outputImage.height();

	for (int x = 0; x < 2; x++) {
		for (int y =  0; y < 2; y++) {
			float samplex = rotationMapping[0][0] * (x*w - halfw) + rotationMapping[1][0] * (y*h - halfh);
			float sampley = rotationMapping[0][1] * (x*w - halfw) + rotationMapping[1][1] * (y*h - halfh);

			samplex += halfw;
			sampley += halfh;

			minx = samplex < minx ? samplex : minx;
			maxx = samplex > maxx ? samplex : maxx;

			miny = sampley < miny ? sampley : miny;
			maxy = sampley > maxy ? sampley : maxy;
		}
	}

	int neww = w - minx + (maxx - w);
	int newh = h - miny + (maxy - h);

	rotatedImage.setSize(neww, newh);
	Pixel sampixel;

	for (int x = 0; x < neww; x++) {
		for (int y = 0; y < newh; y++) {
			float samplex = warpping[0][0] * (x - halfw + minx) + warpping[1][0] * (y - halfh + miny);
			float sampley = warpping[0][1] * (x - halfw + minx) + warpping[1][1] * (y - halfh + miny);

			samplex += halfh;
			sampley += halfh;

			sampixel = outputImage.BilinearSample(samplex, sampley);

			rotatedImage.pixel(x, y).r = sampixel.r;
			rotatedImage.pixel(x, y).g = sampixel.g;
			rotatedImage.pixel(x, y).b = sampixel.b;
		}
	}
	outputImage = rotatedImage;
	return 1;
}
	
int Image::RotateGaussian(const float& angle,Image& outputImage) const
{
	Image rotatedImage;
	float anglerad = -angle / 180 * M_PI;

	float rotationMapping[2][2] = { cos(anglerad), sin(anglerad),
						  -sin(anglerad), cos(anglerad)};

	float warpping[2][2] = { cos(anglerad), -sin(anglerad),
						   sin(anglerad), cos(anglerad)};

	float minx = 0;
	float miny = 0;

	float maxx = outputImage.width();
	float maxy = outputImage.height();

	float halfw = outputImage.width()/2;
	float halfh = outputImage.height()/2;

	float w = outputImage.width();
	float h = outputImage.height();

	for (int x = 0; x < 2; x++) {
		for (int y =  0; y < 2; y++) {
			float samplex = rotationMapping[0][0] * (x*w - halfw) + rotationMapping[1][0] * (y*h - halfh);
			float sampley = rotationMapping[0][1] * (x*w - halfw) + rotationMapping[1][1] * (y*h - halfh);

			samplex += halfw;
			sampley += halfh;

			minx = samplex < minx ? samplex : minx;
			maxx = samplex > maxx ? samplex : maxx;

			miny = sampley < miny ? sampley : miny;
			maxy = sampley > maxy ? sampley : maxy;
		}
	}

	int neww = w - minx + (maxx - w);
	int newh = h - miny + (maxy - h);

	rotatedImage.setSize(neww, newh);
	Pixel sampixel;

	for (int x=0; x<neww; x++) {
		for (int y=0; y<newh; y++) {
			float samplex = warpping[0][0] * (x - halfw + minx) + warpping[1][0] * (y - halfh + miny);
			float sampley = warpping[0][1] * (x - halfw + minx) + warpping[1][1] * (y - halfh + miny);

			samplex += halfh;
			sampley += halfh;

			if ((samplex < 0 || samplex > w - 1) ||
			   (sampley < 0 || sampley > h - 1)) {
				rotatedImage.pixel(x, y).r = 0;
				rotatedImage.pixel(x, y).g = 0;
				rotatedImage.pixel(x, y).b = 0;
			}

			sampixel = outputImage.GaussianSample(samplex, sampley, 0.5, 1);

			rotatedImage.pixel(x, y).r = sampixel.r;
			rotatedImage.pixel(x, y).g = sampixel.g;
			rotatedImage.pixel(x, y).b = sampixel.b;
		}
	}
	outputImage = rotatedImage;
	return 1;
}


int Image::SetAlpha(const Image& matte)
{
	for(int y = 0; y < matte.height(); y++) {
        for(int x = 0; x < matte.width(); x++) {
            Pixel& p = this->pixel(x,y);
            Pixel m = matte.pixel(x,y);
            float alpha = m.r * 0.3 + m.g * 0.59 + m.b * 0.11;
            p.a = alpha;
            p.a = p.a > 255 ? 255 : p.a;
			p.a = p.a < 0 ? 0 : p.a;
        }
    }
	return 1;
	// return 0;
}

int Image::Composite(const Image& overlay,Image& outputImage) const
{
    for(int y = 0; y < outputImage.height(); y++) {
        for(int x = 0; x < outputImage.width(); x++) {

            // check if the point is within the overlay image
			if(x < 0 || x >= overlay.width() || y < 0 || y >= overlay.height()){
				continue;
			}

			// uncomment the math if you need premultiplied images
            outputImage.pixel(x, y).r = /*(float) overlay.pixel(x, y).a / 255 */ overlay.pixel(x, y).r + (1 - (float) overlay.pixel(x, y).a / 255) * this->pixel(x, y).r;
            outputImage.pixel(x, y).r = outputImage.pixel(x, y).r > 255 ? 255 : outputImage.pixel(x, y).r;
			outputImage.pixel(x, y).r = outputImage.pixel(x, y).r < 0 ? 0 : outputImage.pixel(x, y).r;
            
            outputImage.pixel(x, y).g = /*(float) overlay.pixel(x, y).a / 255 */ overlay.pixel(x, y).g + (1 - (float) overlay.pixel(x, y).a / 255) * this->pixel(x, y).g;
            outputImage.pixel(x, y).g = outputImage.pixel(x, y).g > 255 ? 255 : outputImage.pixel(x, y).g;
			outputImage.pixel(x, y).g = outputImage.pixel(x, y).g < 0 ? 0 : outputImage.pixel(x, y).g;

            outputImage.pixel(x, y).b = /*(float) overlay.pixel(x, y).a / 255 */ overlay.pixel(x, y).b + (1 - (float) overlay.pixel(x, y).a / 255) * this->pixel(x, y).b;
            outputImage.pixel(x, y).b = outputImage.pixel(x, y).b > 255 ? 255 : outputImage.pixel(x, y).b;
			outputImage.pixel(x, y).b = outputImage.pixel(x, y).b < 0 ? 0 : outputImage.pixel(x, y).b;
        }
    }
    return 1;
	// return 0;
}

int Image::CrossDissolve(const Image& source,const Image& destination,const float& blendWeight,Image& outputImage)
{
	outputImage.setSize(destination.width(), destination.height());

	for(int y = 0; y < destination.height(); y++) {
		for(int x = 0; x < destination.width(); x++) {
			outputImage.pixel(x, y).r = (1 - blendWeight) * source.pixel(x, y).r + blendWeight * destination.pixel(x, y).r;
			outputImage.pixel(x, y).r = outputImage.pixel(x, y).r > 255 ? 255 : outputImage.pixel(x, y).r;
			outputImage.pixel(x, y).r = outputImage.pixel(x, y).r < 0 ? 0 : outputImage.pixel(x, y).r;

			outputImage.pixel(x, y).g = (1 - blendWeight) * source.pixel(x, y).g + blendWeight * destination.pixel(x, y).g;
			outputImage.pixel(x, y).g = outputImage.pixel(x, y).g > 255 ? 255 : outputImage.pixel(x, y).g;
			outputImage.pixel(x, y).g = outputImage.pixel(x, y).g < 0 ? 0 : outputImage.pixel(x, y).g;

			outputImage.pixel(x, y).b = (1 - blendWeight) * source.pixel(x, y).b + blendWeight * destination.pixel(x, y).b;
			outputImage.pixel(x, y).b = outputImage.pixel(x, y).b > 255 ? 255 : outputImage.pixel(x, y).b;
			outputImage.pixel(x, y).b = outputImage.pixel(x, y).b < 0 ? 0 : outputImage.pixel(x, y).b;
		}
	}
	return 1;
}

int Image::Warp(const OrientedLineSegmentPairs& olsp,Image& outputImage) const
{
	outputImage.setSize(width(), height());

	for (int y = 0; y < height(); y++) {
		for (int x = 0; x < width(); x++) {
			float sourceX, sourceY;
			olsp.getSourcePosition(x, y, sourceX, sourceY);
			outputImage(x, y) = NearestSample(sourceX, sourceY);
		}
	}
	return 1;
}

// fish eye effect
int Image::FishEye(Image& outputImage) const
{
    for (int y = 0; y < outputImage.height(); y++) {                            
        // normalize y coordinate to -1 ... 1
        float ny = ((float) (2 * y)) / ((float) outputImage.height()) - 1;              
        // pre calculate ny*ny
        float ny2 = ny * ny;      
        // for each column
        for (int x = 0; x <outputImage.width(); x++) {
            // normalize x coordinate to -1 ... 1
            float nx = ((float)(2 * x)) / ((float) outputImage.width()) - 1;     
            // pre calculate nx*nx
            float nx2 = nx * nx;
            // calculate distance from center (0,0)
            // this will include circle or ellipse shape portion
            // of the image, depending on image dimensions
            // you can experiment with images with different dimensions
            float r = sqrt(nx2 + ny2);
            // discard pixels outside from circle!
            if (r >= 0 && r <= 1) {        
                float nr = sqrt(1 - r * r);
                // new distance is between 0 ... 1
                nr = (r + (1 - nr)) / 2;
                // discard radius greater than 1.0
                if (nr <= 1) {
                    // calculate the angle for polar coordinates
                    float theta = atan2(ny, nx);
                    // calculate new x position with new distance in same angle
                    float nxn = nr * cos(theta);
                    // calculate new y position with new distance in same angle
                    float nyn = nr * sin(theta);
                    // map from -1 ... 1 to image coordinates
                    int x2 = (int)((nxn + 1) * ((float) outputImage.width()) / 2);
                    // map from -1 ... 1 to image coordinates
                    int y2 = (int)((nyn + 1) * ((float) outputImage.height()) / 2);
                    // find (x2,y2) position from source pixels
                    // check if the point to be sampled is within the image
					if(x2 >= 0 || x2 < w || y2 >= 0 || y2 < h)
                    	outputImage.pixel(x, y) = this->pixel(x2, y2);
                }
            }
        }
    }
    //return result pixels
    return 1;
}

int Image::Crop(const int& x1,const int& y1,const int& x2,const int& y2,Image& outputImage) const
{
	// only crop if 0 =< x1 <x2 <= original_width and 0 =< y1 <y2 <= original_height
	if ( x1 >= 0 && x1 < x2 && x2 <= outputImage.width() && y1 >= 0 && y1 < y2 && y2 <= outputImage.height()) {
		
		Image *croppedImage = new Image();

		croppedImage->setSize(x2-x1, y2-y1);

		for (int y = y1; y < y2; y++) {
			for (int x = x1; x < x2; x++) {
				Pixel &newpixel = outputImage.pixel(x, y);	
				croppedImage->pixel(x-x1, y-y1) = newpixel;
			}
		}
		
		outputImage = *croppedImage;

		return 1;
	}
	return 0;
}


Pixel Image::NearestSample(const float& x,const float& y) const
{
	// check if the point to be sampled is within the image
	if(x < 0 || x >= w || y < 0 || y >= h){
		return Pixel();
	}

	int u = floor(x + 0.5);
	int v = floor(y + 0.5);
	// check if nearest point is within the image
	if(u < 0 || u >= w || v < 0 || v >= h){
		return Pixel();
	}
	return this->pixel(u, v);
}

Pixel Image::BilinearSample(const float& x,const float& y) const
{
	// check if the point to be sampled is within the image
	if(x < 0 || x >= w || y < 0 || y >= h){
		return Pixel();
	}

	// the orientation of all the relative points is the following

	//	(x1, y1)-------b----(x2, y1)
	//     |		   |       |
	//	   |		   |       |
	//	   |		   |       |
	//	   |		   |       |
	//     |         (x, y)    |
	//     |		   |       |
	//     |		   |       |
	//     |		   |       |
	//  (x1, y2)-------a----(x2, y2)

	float x1, x2, y1, y2, dX, dY;

	x1 = floor(x);
	x2 = x1 + 1;
	y1 = floor(y);
	y2 = y1 + 1;
	dX = x - x1;
	dY = y - y1;

	Pixel a, b;

	a.r = this->pixel(x1, y1).r * (1 - dX);
	a.g = this->pixel(x1, y1).g * (1 - dX);
	a.b = this->pixel(x1, y1).b * (1 - dX);

	// all the if statements below check if the 4 surrounding points of (x, y) are within the image
	if (x2 < w) {
		a.r += this->pixel(x2, y1).r * dX;
		a.g += this->pixel(x2, y1).g * dX;
		a.b += this->pixel(x2, y1).b * dX;
	}
	if (y2 < h) {
		b.r = this->pixel(x1, y2).r * (1 - dX);
		b.g = this->pixel(x1, y2).g * (1 - dX);
		b.b = this->pixel(x1, y2).b * (1 - dX);
	}
	if (x2 < w && y2 < h) {
		b.r += this->pixel(x2, y2).r * dX;
		b.g += this->pixel(x2, y2).g * dX;
		b.b += this->pixel(x2, y2).b * dX;
	}

	Pixel dst;
	dst.r = a.r * (1 - dY) + b.r * dY;
	dst.g = a.g * (1 - dY) + b.g * dY;
	dst.b = a.b * (1 - dY) + b.b * dY;
	
	return dst;
}

Pixel Image::GaussianSample(const float& centerX,const float& centerY,const float& variance,const float& radius) const
{
	// check if the point to be sampled is within the image
	if(centerX < 0 || centerX >= w || centerY < 0 || centerY >= h){
		return Pixel();
	}

	float runningR = 0;
	float runningG = 0;
	float runningB = 0;
	float runningTotal = 0;

	for (int y = (centerY - radius); y < (centerY + radius * 2); y++) {
		for (int x = (centerX - radius); x < (centerX + radius * 2); x++) {

			if (y < 0 || x < 0 || x > w - 1 || y > h - 1) {
 				continue;
			}

			float distX = fabs(centerX - (x + 0.5));
			float distY = fabs(centerY - (y + 0.5));

			if (distX > radius || distY > radius) {
				 continue;
			}

			float norm = pow(M_E, -(distX*distX + distY*distY)/(2*pow(pow(variance, 0.5)*radius, 2)));
			runningTotal += norm;

			runningR += norm * this->pixel(x, y).r;
			runningG += norm * this->pixel(x, y).g;
			runningB += norm * this->pixel(x, y).b;
		}
	}

	Pixel pixel;
	pixel.r = runningR/runningTotal;
	pixel.g = runningG/runningTotal;
	pixel.b = runningB/runningTotal;
	return pixel;
}
